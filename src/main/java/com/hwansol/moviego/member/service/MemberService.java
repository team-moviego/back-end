package com.hwansol.moviego.member.service;

import com.hwansol.moviego.auth.TokenProvider;
import com.hwansol.moviego.mail.service.MailService;
import com.hwansol.moviego.mail.service.MailType;
import com.hwansol.moviego.member.dto.MemberSignInDto;
import com.hwansol.moviego.member.dto.MemberSignupDto;
import com.hwansol.moviego.member.exception.MemberErrorCode;
import com.hwansol.moviego.member.exception.MemberException;
import com.hwansol.moviego.member.model.Member;
import com.hwansol.moviego.member.model.Role;
import com.hwansol.moviego.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
//todo: 카카오 회원인지 구분하는 로직 구현 필요
public class MemberService {

    private static final String AUTH_NUM_KEY = "auth:";
    private static final String IS_AUTH_KEY = "isAuth:";

    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    /**
     * 아이디 중복 확인 서비스
     *
     * @param userId - 사용할 아이디
     */
    public void duplicatedId(String userId) {
        boolean isDuplicated = memberRepository.existsByUserId(userId);

        if (!isDuplicated) {
            throw new MemberException(MemberErrorCode.DUPLICATED_ID);
        }
    }

    /**
     * 이메일 중복 확인 서비스
     *
     * @param userEmail - 사용할 이메일
     */
    public void duplicatedEmail(String userEmail) {
        boolean isDuplicated = memberRepository.existsByUserEmail(userEmail);

        if (!isDuplicated) {
            throw new MemberException(MemberErrorCode.DUPLICATED_EMAIL);
        }
    }

    /**
     * 아이디 찾기 서비스
     *
     * @param userEmail - 아이디 전송할 회원 이메일 주소
     */
    public void findId(String userEmail) {
        Member member = memberRepository.findByUserEmail(userEmail)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        String userId = member.getUserId();
        mailService.sendEmail(userEmail, userId, MailType.ID);
    }

    /**
     * 비밀번호 찾기 서비스
     *
     * @param userId    - 비밀번호를 찾을 회원 아이디
     * @param userEmail - 임시비밀번호를 발송할 회원 이메일
     */
    public void findPw(String userId, String userEmail) {
        Member member = memberRepository.findByUserId(userId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        String temporaryPw = UUID.randomUUID().toString().substring(0, 7); // 8자리 임시 비밀번호
        String newPw = passwordEncoder.encode(temporaryPw);

        member = member.toBuilder()
            .userPw(newPw)
            .build();

        memberRepository.save(member);

        mailService.sendEmail(userEmail, temporaryPw, MailType.PW);
    }

    /**
     * 회원 조회 서비스
     *
     * @param userId 조회할 회원 아이디
     * @return 조회된 회원 엔티티
     */
    public Member getMember(String userId) {
        return memberRepository.findByUserId(userId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));
    }

    /**
     * 인증번호 이메일 발송 서비스
     *
     * @param userEmail - 인증번호 발송할 회원 이메일 주소
     */
    public void sendAuthNum(String userEmail) {
        String authNum = createAuthNum();
        redisTemplate.opsForValue().set(AUTH_NUM_KEY + userEmail, authNum, Duration.ofMinutes(5));
        redisTemplate.opsForValue().set(IS_AUTH_KEY + userEmail, "false", Duration.ofMinutes(5));

        mailService.sendEmail(userEmail, authNum, MailType.AUTH);
    }

    /**
     * 인증번호 확인 서비스
     *
     * @param userEmail - 회원 이메일
     * @param authNum   - 회원이 입력한 인증번호
     */
    public void checkAuthNum(String userEmail, String authNum) {
        String originAuthNum = redisTemplate.opsForValue().get(AUTH_NUM_KEY + userEmail);

        if (originAuthNum == null) {
            throw new MemberException(MemberErrorCode.TIME_OVER_AUTH);
        }

        if (!originAuthNum.equals(authNum)) {
            throw new MemberException(MemberErrorCode.WRONG_AUTH_NUM);
        }
    }

    /**
     * 회원가입 서비스
     *
     * @param request - MemberSignupDto.Request
     * @return 회원가입된 엔티티
     */
    public Member signup(MemberSignupDto.Request request) {

        String encodedPw = passwordEncoder.encode(request.getUserPw());

        Member member = Member.builder()
            .userId(request.getUserId())
            .userPw(encodedPw)
            .userEmail(request.getUserEmail())
            .role(Role.ROLE_USER)
            .build();

        return memberRepository.save(member);
    }

    /**
     * 일반 로그인 서비스
     *
     * @param request  MemberSignUpDto.Request
     * @param response ServletResponse
     * @return 생성된 accessToken
     */
    public String signIn(MemberSignInDto.Request request, HttpServletResponse response) {
        Member member = memberRepository.findByUserId(request.getUserId())
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        if (!passwordEncoder.matches(request.getUserPw(), member.getUserPw())) {
            throw new MemberException(MemberErrorCode.WRONG_PASSWORD);
        }

        tokenProvider.generateRefreshToken(member.getUserId(), List.of(member.getRole().getName()),
            response);

        return tokenProvider.generateAccessToken(member.getUserId(),
            List.of(member.getRole().getName()));
    }

    private String createAuthNum() {
        SecureRandom sr = new SecureRandom();
        int random = sr.nextInt(1_000_000); // 1~999999 랜덤 수 생성

        return String.format("%06d", random); // 앞자리 0을 포함한 6자리 문자열로 반환
    }
}
