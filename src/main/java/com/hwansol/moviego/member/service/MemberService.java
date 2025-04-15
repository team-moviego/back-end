package com.hwansol.moviego.member.service;

import com.hwansol.moviego.mail.service.MailService;
import com.hwansol.moviego.mail.service.MailType;
import com.hwansol.moviego.member.dto.MemberSignupDto;
import com.hwansol.moviego.member.exception.MemberErrorCode;
import com.hwansol.moviego.member.exception.MemberException;
import com.hwansol.moviego.member.model.Member;
import com.hwansol.moviego.member.model.Role;
import com.hwansol.moviego.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final MemberRepository memberRepository;

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
    //todo: 카카오 회원인지 구분하는 로직 구현 필요
    public void findId(String userEmail) {
        Member member = memberRepository.findByUserEmail(userEmail)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        String userId = member.getUserId();
        mailService.sendEmail(userEmail, userId, MailType.ID);
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
}
