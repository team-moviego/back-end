package com.hwansol.moviego.member.service;

import com.hwansol.moviego.member.exception.MemberErrorCode;
import com.hwansol.moviego.member.exception.MemberException;
import com.hwansol.moviego.member.model.Member;
import com.hwansol.moviego.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

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
}
