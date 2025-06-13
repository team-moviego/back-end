package com.hwansol.moviego.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hwansol.moviego.auth.TokenProvider;
import com.hwansol.moviego.mail.service.MailService;
import com.hwansol.moviego.mail.service.MailType;
import com.hwansol.moviego.member.dto.MemberAuthDto;
import com.hwansol.moviego.member.dto.MemberModifyEmailDto;
import com.hwansol.moviego.member.dto.MemberModifyPwDto;
import com.hwansol.moviego.member.dto.MemberSignInDto;
import com.hwansol.moviego.member.dto.MemberSignupDto;
import com.hwansol.moviego.member.dto.MemberSignupDto.Request;
import com.hwansol.moviego.member.exception.MemberErrorCode;
import com.hwansol.moviego.member.exception.MemberException;
import com.hwansol.moviego.member.model.Member;
import com.hwansol.moviego.member.model.OAuthProvider;
import com.hwansol.moviego.member.model.Role;
import com.hwansol.moviego.member.repository.MemberRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private MockHttpServletResponse mockHttpServletResponse;

    @Mock
    private MockHttpServletRequest mockHttpServletRequest;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("아이디 중복 확인")
    void duplicatedId() {
        String userId = "test";

        when(memberRepository.existsByUserId(userId)).thenReturn(false);

        assertDoesNotThrow(() -> memberService.duplicatedId(userId));
    }

    @Test
    @DisplayName("아이디 중복 확인 실패 - 중복된 아이디")
    void duplicatedIdFail1() {
        String userId = "test";

        when(memberRepository.existsByUserId(userId)).thenReturn(true);

        assertThrows(MemberException.class, () -> memberService.duplicatedId(userId),
            MemberErrorCode.DUPLICATED_ID.getMessage());
    }

    @Test
    @DisplayName("이메일 중복 확인")
    void duplicatedEmail() {
        String userEmail = "test@naver.com";

        when(memberRepository.existsByUserEmail(userEmail)).thenReturn(false);

        assertDoesNotThrow(() -> memberService.duplicatedEmail(userEmail));
    }

    @Test
    @DisplayName("이메일 중복 확인 실패 - 이메일 중복")
    void duplicatedEmailFail1() {
        String userEmail = "test@naver.com";

        when(memberRepository.existsByUserEmail(userEmail)).thenReturn(true);

        assertThrows(MemberException.class, () -> memberService.duplicatedEmail(userEmail),
            MemberErrorCode.DUPLICATED_EMAIL.getMessage());
    }

    @Test
    @DisplayName("이메일 중복 확인 실패 - 카카오 회원인 경우")
    void duplicatedEmailFail2() {
        Member member = Member.builder()
            .userEmail("test@gmail.com")
            .oAuthProvider(OAuthProvider.KAKAO)
            .build();

        when(memberRepository.existsByUserEmail(member.getUserEmail())).thenReturn(true);
        when(memberRepository.findByUserEmail(member.getUserEmail())).thenReturn(
            Optional.of(member));

        assertThrows(MemberException.class,
            () -> memberService.duplicatedEmail(member.getUserEmail()),
            MemberErrorCode.SOCIAL_USER.getMessage());
    }

    @Test
    @DisplayName("아이디 찾기")
    void findId() {
        Member member = Member.builder()
            .userEmail("test@naver.com")
            .userId("test")
            .build();

        when(memberRepository.findByUserEmail("test@naver.com")).thenReturn(
            Optional.of(member));

        Member result = memberService.findId("test@naver.com");

        assertThat(result.getUserId()).isEqualTo("test");
    }

    @Test
    @DisplayName("아이디 찾기 실패 - 없는 회원")
    void findIdFail1() {
        when(memberRepository.findByUserEmail("test@naver.com")).thenReturn(Optional.empty());

        assertThrows(MemberException.class, () -> memberService.findId("test@naver.com"),
            MemberErrorCode.NOT_FOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("아이디 찾기 실패 - 카카오 회원인 경우")
    void findIdFail2() {
        Member member = Member.builder()
            .userEmail("test@naver.com")
            .oAuthProvider(OAuthProvider.KAKAO)
            .build();

        when(memberRepository.findByUserEmail(member.getUserEmail())).thenReturn(
            Optional.of(member));

        assertThrows(MemberException.class, () -> memberService.findId(member.getUserEmail()),
            MemberErrorCode.SOCIAL_USER.getMessage());
    }

    @Test
    @DisplayName("비밀번호 찾기")
    void findPw() {
        Member member = Member.builder()
            .userId("test")
            .userPw("abc12345")
            .build();

        when(memberRepository.findByUserId("test")).thenReturn(Optional.of(member));
        when(passwordEncoder.encode(argThat(s -> s.length() == 8))).thenReturn("Pds83iL2");

        memberService.findPw("test", "test@naver.com");

        verify(memberRepository, times(1)).save(argThat(m -> m.getUserPw().equals("Pds83iL2")));
        verify(mailService, times(1)).sendEmail(argThat(m -> m.equals("test@naver.com")),
            argThat(s -> s.length() == 8),
            argThat(e -> e.name().equals("PW")));
    }

    @Test
    @DisplayName("비밀번호 찾기 실패 - 없는 회원")
    void findPwFail1() {
        when(memberRepository.findByUserId("test")).thenReturn(Optional.empty());

        assertThrows(MemberException.class, () -> memberService.findPw("test", "test@naver.com"),
            MemberErrorCode.NOT_FOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("비밀번호 찾기 실패 - 카카오 회원인 경우")
    void findPwFail2() {
        Member member = Member.builder()
            .userId("test")
            .userEmail("test@naver.com")
            .oAuthProvider(OAuthProvider.KAKAO)
            .build();

        when(memberRepository.findByUserId(member.getUserId())).thenReturn(Optional.of(member));

        assertThrows(MemberException.class,
            () -> memberService.findPw(member.getUserId(), member.getUserEmail()),
            MemberErrorCode.SOCIAL_USER.getMessage());
    }

    @Test
    @DisplayName("회원 조회")
    void getMember() {
        Member member = Member.builder()
            .userId("test")
            .build();

        when(memberRepository.findByUserId("test")).thenReturn(Optional.of(member));

        Member result = memberService.getMember("test");

        assertThat(result.getUserId()).isEqualTo("test");
    }

    @Test
    @DisplayName("회원 조회 실패 - 존재하지 않는 회원")
    void getMemberFail1() {
        when(memberRepository.findByUserId("test")).thenReturn(Optional.empty());

        assertThrows(MemberException.class, () -> memberService.getMember("test"),
            MemberErrorCode.NOT_FOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("인증번호 이메일 발송")
    void sendAuthNum() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations)
            .set(eq("auth:test@naver.com"), argThat(s -> s.length() == 6),
                eq(Duration.ofMinutes(5)));
        doNothing().when(valueOperations)
            .set("isAuth:test@naver.com", "false", Duration.ofMinutes(5));

        memberService.sendAuthNum("test@naver.com");

        verify(valueOperations, times(1)).set(eq("auth:test@naver.com"),
            argThat(s -> s.length() == 6), eq(Duration.ofMinutes(5)));
        verify(valueOperations, times(1)).set("isAuth:test@naver.com", "false",
            Duration.ofMinutes(5));
        verify(mailService, times(1)).sendEmail(eq("test@naver.com"), argThat(s -> s.length() == 6),
            eq(
                MailType.AUTH));
    }

    @Test
    @DisplayName("인증번호 확인 서비스")
    void checkAuthNum() {
        MemberAuthDto.Request request = MemberAuthDto.Request.builder()
            .authNum("123456")
            .userEmail("test@naver.com")
            .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("auth:test@naver.com")).thenReturn("123456");

        memberService.checkAuthNum(request);

        verify(valueOperations, times(1)).set("isAuth:test@naver.com", "true");
    }

    @Test
    @DisplayName("인증번호 확인 서비스 실패 - 인증 번호 입력 시간 초과")
    void checkAuthNumFail1() {
        MemberAuthDto.Request request = MemberAuthDto.Request.builder()
            .userEmail("test@naver.com")
            .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("auth:test@naver.com")).thenReturn(null);

        assertThrows(MemberException.class, () -> memberService.checkAuthNum(request),
            MemberErrorCode.TIME_OVER_AUTH.getMessage());
    }

    @Test
    @DisplayName("인증번호 확인 서비스 실패 - 인증 번호 불일치")
    void checkAuthNumFail2() {
        MemberAuthDto.Request request = MemberAuthDto.Request.builder()
            .userEmail("test@naver.com")
            .authNum("234567")
            .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("auth:test@naver.com")).thenReturn("123455");

        assertThrows(MemberException.class, () -> memberService.checkAuthNum(request),
            MemberErrorCode.WRONG_AUTH_NUM.getMessage());
    }

    @Test
    @DisplayName("회원가입 서비스")
    void signup() {
        MemberSignupDto.Request request = MemberSignupDto.Request.builder()
            .userId("test")
            .userPw("pw")
            .userEmail("test@naver.com")
            .confirmPw("pw")
            .build();
        Member member = Member.builder()
            .userEmail("test@naver.com")
            .userPw("pw")
            .role(Role.ROLE_USER)
            .userId("test")
            .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("isAuth:test@naver.com")).thenReturn("true");
        when(passwordEncoder.encode("pw")).thenReturn("pw");
        when(memberRepository.save(argThat(m -> m.getUserId().equals("test")))).thenReturn(member);

        Member result = memberService.signup(request);

        assertThat(result.getUserId()).isEqualTo("test");
        assertThat(result.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(result.getUserPw()).isEqualTo("pw");
        assertThat(result.getUserEmail()).isEqualTo("test@naver.com");
    }

    @Test
    @DisplayName("회원가입 서비스 실패 - 인증번호 불일치")
    void signupFail1() {
        Request request = Request.builder()
            .userEmail("test@naver.com")
            .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("isAuth:test@naver.com")).thenReturn(null);

        assertThrows(MemberException.class, () -> memberService.signup(request),
            MemberErrorCode.NOT_COMPLETED_AUTH.getMessage());
    }

    @Test
    @DisplayName("회원가입 서비스 실패 - 비밀번호 확인 불일치")
    void signupFail2() {
        Request request = Request.builder()
            .userEmail("test@naver.com")
            .userPw("pw")
            .confirmPw("pa")
            .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("isAuth:test@naver.com")).thenReturn("true");

        assertThrows(MemberException.class, () -> memberService.signup(request),
            MemberErrorCode.DIFF_PW_AND_CONFIRM.getMessage());
    }

    @Test
    @DisplayName("일반 로그인 서비스")
    void signIn() {
        MemberSignInDto.Request request = MemberSignInDto.Request.builder()
            .userId("test")
            .userPw("pw")
            .build();
        Member member = Member.builder()
            .userPw("pw")
            .userId("test")
            .role(Role.ROLE_USER)
            .build();

        when(memberRepository.findByUserId("test")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("pw", "pw")).thenReturn(true);
        when(tokenProvider.generateAccessToken("test", List.of("USER"))).thenReturn("token");

        String token = memberService.signIn(request, mockHttpServletResponse);

        assertThat(token).isEqualTo("token");
    }

    @Test
    @DisplayName("일반 로그인 서비스 실패 - 존재하지 않는 회원")
    void signInFail1() {
        MemberSignInDto.Request request = MemberSignInDto.Request.builder()
            .userId("test")
            .build();

        when(memberRepository.findByUserId("test")).thenReturn(Optional.empty());

        assertThrows(MemberException.class,
            () -> memberService.signIn(request, mockHttpServletResponse),
            MemberErrorCode.NOT_FOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("일반 로그인 서비스 실패 - 비밀번호 불일치")
    void signInFail2() {
        MemberSignInDto.Request request = MemberSignInDto.Request.builder()
            .userId("test")
            .userPw("pw")
            .build();
        Member member = Member.builder()
            .userPw("pa")
            .build();

        when(memberRepository.findByUserId("test")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("pw", "pa")).thenReturn(false);

        assertThrows(MemberException.class,
            () -> memberService.signIn(request, mockHttpServletResponse),
            MemberErrorCode.WRONG_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("회원 로그아웃 서비스")
    void signOut() {
        memberService.signOut(mockHttpServletRequest, mockHttpServletResponse);

        verify(tokenProvider, times(1)).logout(mockHttpServletRequest, mockHttpServletResponse);
    }

    @Test
    @DisplayName("회원 이메일 변경 서비스")
    void modifyEmail() {
        MemberModifyEmailDto.Request request = MemberModifyEmailDto.Request.builder()
            .originEmail("test@naver.com")
            .newEmail("test@gmail.com")
            .build();
        Member member = Member.builder()
            .userPw("pw")
            .role(Role.ROLE_USER)
            .userId("test")
            .userEmail("test@naver.com")
            .build();

        when(memberRepository.findByUserEmail("test@naver.com")).thenReturn(Optional.of(member));
        when(memberRepository.existsByUserEmail("test@gmail.com")).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("isAuth:test@gmail.com")).thenReturn("true");
        when(memberRepository.save(
            argThat(m -> m.getUserEmail().equals("test@gmail.com")))).thenReturn(
            member.toBuilder().userEmail("test@gmail.com").build());

        Member result = memberService.modifyEmail(request);

        assertThat(result.getUserEmail()).isEqualTo("test@gmail.com");

        verify(redisTemplate, times(1)).delete("isAuth:test@gmail.com");
    }

    @Test
    @DisplayName("회원 이메일 변경 서비스 실패 - 존재하지 않는 회원")
    void modifyEmailFail1() {
        MemberModifyEmailDto.Request request = MemberModifyEmailDto.Request.builder()
            .originEmail("test@naver.com")
            .build();

        when(memberRepository.findByUserEmail("test@naver.com")).thenReturn(Optional.empty());

        assertThrows(MemberException.class, () -> memberService.modifyEmail(request),
            MemberErrorCode.NOT_FOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("회원 이메일 변경 서비스 실패 - 변경할 이메일과 원래 이메일 일치")
    void modifyEmailFail2() {
        MemberModifyEmailDto.Request request = MemberModifyEmailDto.Request.builder()
            .originEmail("test@naver.com")
            .newEmail("test@naver.com")
            .build();
        Member member = Member.builder()
            .userEmail("test@naver.com")
            .build();

        when(memberRepository.findByUserEmail(request.getOriginEmail())).thenReturn(
            Optional.of(member));

        assertThrows(MemberException.class, () -> memberService.modifyEmail(request),
            MemberErrorCode.ORIGIN_EQUALS_NEW_OF_EMAIL.getMessage());
    }

    @Test
    @DisplayName("회원 이메일 변경 서비스 실패 - 변경할 이메일이 이미 사용중인 이메일")
    void modifyEmailFail3() {
        MemberModifyEmailDto.Request request = MemberModifyEmailDto.Request.builder()
            .originEmail("test@naver.com")
            .newEmail("test@gmail.com")
            .build();
        Member member = Member.builder()
            .userEmail("test@naver.com")
            .build();

        when(memberRepository.findByUserEmail(request.getOriginEmail())).thenReturn(
            Optional.of(member));
        when(memberRepository.existsByUserEmail(request.getNewEmail())).thenReturn(true);

        assertThrows(MemberException.class, () -> memberService.modifyEmail(request),
            MemberErrorCode.DUPLICATED_EMAIL.getMessage());
    }

    @Test
    @DisplayName("회원 이메일 변경 서비스 실패 - 변경할 이메일로 인증 진행을 하지 않은 경우")
    void modifyEmailFail4() {
        MemberModifyEmailDto.Request request = MemberModifyEmailDto.Request.builder()
            .originEmail("test@naver.com")
            .newEmail("test@gmail.com")
            .build();
        Member member = Member.builder()
            .userEmail("test@naver.com")
            .build();

        when(memberRepository.findByUserEmail(request.getOriginEmail())).thenReturn(
            Optional.of(member));
        when(memberRepository.existsByUserEmail(request.getNewEmail())).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("isAuth:test@gmail.com")).thenReturn(null);

        assertThrows(MemberException.class, () -> memberService.modifyEmail(request),
            MemberErrorCode.NOT_COMPLETED_AUTH.getMessage());
    }

    @Test
    @DisplayName("회원 이메일 변경 서비스 실패 - 카카오 회원인 경우")
    void modifyEmailFail5() {
        Member member = Member.builder()
            .userEmail("test@naver.com")
            .oAuthProvider(OAuthProvider.KAKAO)
            .build();
        MemberModifyEmailDto.Request request = MemberModifyEmailDto.Request.builder()
            .newEmail("test@gmail.com")
            .originEmail("test@naver.com")
            .build();

        when(memberRepository.findByUserEmail(member.getUserEmail())).thenReturn(
            Optional.of(member));

        assertThrows(MemberException.class, () -> memberService.modifyEmail(request),
            MemberErrorCode.SOCIAL_USER.getMessage());
    }

    @Test
    @DisplayName("회원 비밀번호 변경 서비스")
    void modifyPw() {
        MemberModifyPwDto.Request request = MemberModifyPwDto.Request.builder()
            .newPw("pa")
            .confirmPw("pa")
            .originPw("pw")
            .build();
        Member member = Member.builder()
            .userPw("pw")
            .build();

        when(memberRepository.findByUserId("test")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(request.getOriginPw(), member.getUserPw())).thenReturn(true);
        when(passwordEncoder.encode(request.getNewPw())).thenReturn("pa");
        when(memberRepository.save(argThat(m -> m.getUserPw().equals("pa")))).thenReturn(
            member.toBuilder().userPw("pa").build());

        Member result = memberService.modifyPw("test", request);

        assertThat(result.getUserPw()).isEqualTo("pa");
    }

    @Test
    @DisplayName("회원 비밀번호 변경 서비스 실패 - 존재하지 않는 회원")
    void modifyPwFail1() {
        MemberModifyPwDto.Request request = MemberModifyPwDto.Request.builder()
            .build();

        when(memberRepository.findByUserId("test")).thenReturn(Optional.empty());

        assertThrows(MemberException.class, () -> memberService.modifyPw("test", request),
            MemberErrorCode.NOT_FOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("회원 비밀번호 변경 서비스 실패 - 기존의 비밀번호 불일치")
    void modifyPwFail2() {
        MemberModifyPwDto.Request request = MemberModifyPwDto.Request.builder()
            .originPw("pa")
            .build();
        Member member = Member.builder()
            .userPw("pw")
            .build();

        when(memberRepository.findByUserId("test")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(request.getOriginPw(), member.getUserPw())).thenReturn(false);

        assertThrows(MemberException.class, () -> memberService.modifyPw("test", request),
            MemberErrorCode.WRONG_ORIGIN_PW.getMessage());
    }

    @Test
    @DisplayName("회원 비밀번호 변경 서비스 실패 - 새로운 비밀번호 확인 입력 불일치")
    void modifyPwFail3() {
        MemberModifyPwDto.Request request = MemberModifyPwDto.Request.builder()
            .originPw("pw")
            .newPw("pa")
            .confirmPw("pw")
            .build();
        Member member = Member.builder()
            .userPw("pw")
            .build();

        when(memberRepository.findByUserId("test")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(request.getOriginPw(), member.getUserPw())).thenReturn(true);

        assertThrows(MemberException.class, () -> memberService.modifyPw("test", request),
            MemberErrorCode.DIFF_PW_AND_CONFIRM.getMessage());
    }

    @Test
    @DisplayName("회원 비밀번호 변경 서비스 실패 - 카카오 회원인 경우")
    void modifyPwFail4() {
        Member member = Member.builder()
            .userId("test")
            .oAuthProvider(OAuthProvider.KAKAO)
            .build();
        MemberModifyPwDto.Request request = MemberModifyPwDto.Request.builder()
            .newPw("pa")
            .originPw("pw")
            .confirmPw("pa")
            .build();

        when(memberRepository.findByUserId(member.getUserId())).thenReturn(Optional.of(member));

        assertThrows(MemberException.class,
            () -> memberService.modifyPw(member.getUserId(), request),
            MemberErrorCode.SOCIAL_USER.getMessage());
    }

    @Test
    @DisplayName("회원탈퇴 서비스")
    void deleteMember() {
        Member member = Member.builder()
            .build();

        when(memberRepository.findByUserId("test")).thenReturn(Optional.of(member));
        when(memberRepository.save(argThat(m -> m.getDelDate() != null))).thenReturn(
            member.toBuilder().delDate(
                LocalDateTime.now()).build());

        Member result = memberService.deleteMember("test", mockHttpServletRequest,
            mockHttpServletResponse);

        assertThat(result.getDelDate()).isNotNull();

        verify(tokenProvider, times(1)).logout(mockHttpServletRequest, mockHttpServletResponse);
    }

    @Test
    @DisplayName("회원탈퇴 서비스 실패 - 존재하지 않는 회원")
    void deleteMemberFail1() {
        when(memberRepository.findByUserId("test")).thenReturn(Optional.empty());

        assertThrows(MemberException.class,
            () -> memberService.deleteMember("test", mockHttpServletRequest,
                mockHttpServletResponse), MemberErrorCode.NOT_FOUND_MEMBER.getMessage());
    }
}