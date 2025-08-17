package com.hwansol.moviego.member.service;

import com.hwansol.moviego.auth.TokenProvider;
import com.hwansol.moviego.mail.service.MailService;
import com.hwansol.moviego.member.dto.MemberAuthCheckDto;
import com.hwansol.moviego.member.dto.MemberAuthMailDto;
import com.hwansol.moviego.member.dto.MemberModifyEmailDto;
import com.hwansol.moviego.member.dto.MemberModifyPwDto;
import com.hwansol.moviego.member.dto.MemberSignInDto;
import com.hwansol.moviego.member.dto.MemberSignupDto;
import com.hwansol.moviego.member.exception.MemberErrorCode;
import com.hwansol.moviego.member.exception.MemberException;
import com.hwansol.moviego.member.model.Member;
import com.hwansol.moviego.member.model.OAuthProvider;
import com.hwansol.moviego.member.model.Role;
import com.hwansol.moviego.member.repository.MemberRepository;
import com.hwansol.moviego.redis.service.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    /**
     * 아이디 중복 확인 서비스
     *
     * @param userId - 사용할 아이디
     */
    @Transactional(readOnly = true)
    public void duplicatedId(String userId) {
        boolean isDuplicated = memberRepository.existsByUserId(userId);

        if (isDuplicated) {
            throw new MemberException(MemberErrorCode.DUPLICATED_ID);
        }
    }

    /**
     * 이메일 중복 확인 서비스
     *
     * @param userEmail - 사용할 이메일
     */
    @Transactional(readOnly = true)
    public void duplicatedEmail(String userEmail) {
        boolean isDuplicated = memberRepository.existsByUserEmail(userEmail);

        if (isDuplicated) {
            Member member = memberRepository.findByUserEmail(userEmail)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

            isKakaoUser(member);

            throw new MemberException(MemberErrorCode.DUPLICATED_EMAIL);
        }
    }

    /**
     * 아이디 찾기 서비스
     *
     * @param userEmail 회원이메일
     * @return 회원 엔티티
     */
    @Transactional(readOnly = true)
    public Member findId(String userEmail) {
        Member member = memberRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        isKakaoUser(member);

        String userId = member.getUserId();
        mailService.sendIdMail(userEmail, userId);

        return member;
    }

    /**
     * 비밀번호 찾기 서비스
     *
     * @param userId    - 비밀번호를 찾을 회원 아이디
     * @param userEmail - 임시비밀번호를 발송할 회원 이메일
     */
    @Transactional
    public void findPw(String userId, String userEmail) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        isKakaoUser(member);

        String temporaryPw = UUID.randomUUID().toString().substring(0, 8); // 8자리 임시 비밀번호
        String newPw = passwordEncoder.encode(temporaryPw);

        member = member.toBuilder()
                .userPw(newPw)
                .build();

        memberRepository.save(member);

        mailService.sendPwMail(userEmail, temporaryPw);
    }

    /**
     * 인증번호 이메일 발송 서비스
     *
     * @param request - MemberAuthMailDto.Request
     */
    public void sendAuthNum(MemberAuthMailDto.Request request) {
        String authNum = createAuthNum();

        mailService.sendAuthMail(request.getUserEmail(), authNum);

        redisService.setAuthNumToRedis(request.getUserEmail(), authNum);
        redisService.setIsAuthToRedis(request.getUserEmail(), "false");
    }

    /**
     * 인증번호 확인 서비스
     *
     * @param request MemberAuthDto.Request
     */
    public void checkAuthNum(MemberAuthCheckDto.Request request) {
        String redisAuthNum = redisService.getAuthNumFromRedis(request.getUserEmail());

        if (!redisAuthNum.equals(request.getAuthNum())) {
            throw new MemberException(MemberErrorCode.WRONG_AUTH_NUM);
        }

        redisService.deleteAuthNumFromRedis(request.getUserEmail());
        redisService.setIsAuthToRedis(request.getUserEmail(), "true");
    }

    /**
     * 회원 조회 서비스
     *
     * @param userId 조회할 회원 아이디
     * @return 조회된 회원 엔티티
     */
    @Transactional(readOnly = true)
    public Member getMember(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));
    }

    /**
     * 회원가입 서비스
     *
     * @param request - MemberSignupDto.Request
     * @return 회원가입된 엔티티
     */
    public Member signup(MemberSignupDto.Request request) {
        validatedInSignUp(request);

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

    /**
     * 회원 로그아웃 서비스
     *
     * @param userId   회원 아이디
     * @param request  ServletRequest
     * @param response ServletResponse
     */
    public void signOut(String userId, HttpServletRequest request, HttpServletResponse response) {
        tokenProvider.logout(userId, request, response);
    }

    /**
     * 회원 이메일 변경 서비스
     *
     * @param request MemberModifyEmailDto.Request
     * @return 이메일 변경 후 저장된 회원 엔티티
     */
    public Member modifyEmail(MemberModifyEmailDto.Request request) {
        Member member = validatedInModifyEmail(request);

        member = member.toBuilder()
                .userEmail(request.getNewEmail())
                .build();

        return memberRepository.save(member);
    }

    /**
     * 회원 비밀번호 변경 서비스
     *
     * @param userId  회원 아이디
     * @param request MemberModifyPwDto.Request
     * @return 비밀번호가 변경된 회원 엔티티
     */
    public Member modifyPw(String userId, MemberModifyPwDto.Request request) {
        Member member = validatedInModifyPw(userId, request);

        String encodedPw = passwordEncoder.encode(request.getNewPw());

        member = member.toBuilder()
                .userPw(encodedPw)
                .build();

        return memberRepository.save(member);
    }

    /**
     * 회원탈퇴 서비스
     *
     * @param userId   회원 아이디
     * @param request  ServletRequest
     * @param response ServletResponse
     * @return 탈퇴 처리된 회원 엔티티
     */
    public Member deleteMember(String userId, HttpServletRequest request,
                               HttpServletResponse response) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        if (member.getDeletedAt() != null) {
            throw new MemberException(MemberErrorCode.ALREADY_DELETED);
        }

        member = member.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();
        Member result = memberRepository.save(member);

        tokenProvider.logout(userId, request, response);

        return result;
    }

    // 카카오 회원 판별 메소드
    private void isKakaoUser(Member member) {
        if (member.getOAuthProvider() != null && member.getOAuthProvider()
                .equals(OAuthProvider.KAKAO)) {
            throw new MemberException(MemberErrorCode.SOCIAL_USER);
        }
    }

    // 회원가입 시 validate를 위한 메소드
    private void validatedInSignUp(MemberSignupDto.Request request) {
        String isAuth = redisService.getIsAuthFromRedis(request.getUserEmail());
        if (isAuth == null || !isAuth.equals("true")) {
            throw new MemberException(MemberErrorCode.NOT_COMPLETED_AUTH);
        }

        if (!request.getUserPw().equals(request.getConfirmPw())) {
            throw new MemberException(MemberErrorCode.DIFF_PW_AND_CONFIRM);
        }

        redisService.deleteIsAuthFromRedis(request.getUserEmail());
    }

    // 회원 이메일 변경 시 validate를 위한 메소드
    private Member validatedInModifyEmail(MemberModifyEmailDto.Request request) {
        Member member = memberRepository.findByUserEmail(request.getOriginEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        isKakaoUser(member);

        if (member.getUserEmail().equals(request.getNewEmail())) {
            throw new MemberException(MemberErrorCode.ORIGIN_EQUALS_NEW_OF_EMAIL);
        }

        if (memberRepository.existsByUserEmail(request.getNewEmail())) {
            throw new MemberException(MemberErrorCode.DUPLICATED_EMAIL);
        }

        String isAuth = redisService.getIsAuthFromRedis(request.getNewEmail());
        if (!isAuth.equals("true")) {
            throw new MemberException(MemberErrorCode.NOT_COMPLETED_AUTH);
        }

        redisService.deleteIsAuthFromRedis(request.getNewEmail());

        return member;
    }

    // 회원 비밀번호 변경 시 validate를 위한 메소드
    private Member validatedInModifyPw(String userId, MemberModifyPwDto.Request request) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        isKakaoUser(member);

        if (!passwordEncoder.matches(request.getOriginPw(), member.getUserPw())) {
            throw new MemberException(MemberErrorCode.WRONG_ORIGIN_PW);
        }

        if (!request.getNewPw().equals(request.getConfirmPw())) {
            throw new MemberException(MemberErrorCode.DIFF_PW_AND_CONFIRM);
        }

        return member;
    }

    // 인증번호 생성 메소드
    private String createAuthNum() {
        SecureRandom sr = new SecureRandom();
        int random = sr.nextInt(1_000_000); // 1~999999 랜덤 수 생성

        return String.format("%06d", random); // 앞자리 0을 포함한 6자리 문자열로 반환
    }
}
