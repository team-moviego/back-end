package com.hwansol.moviego.member.service;

import com.hwansol.moviego.member.dto.OAuth2UserInfo;
import com.hwansol.moviego.member.model.Member;
import com.hwansol.moviego.member.model.OAuthProvider;
import com.hwansol.moviego.member.model.PrincipalDetails;
import com.hwansol.moviego.member.model.Role;
import com.hwansol.moviego.member.repository.MemberRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // 1. 유저 정보(attributes) 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        // 2. 유저 정보 dto 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.ofKakao(oAuth2UserAttributes);

        // 3. 회원가입 및 로그인
        Member member = getOrSave(oAuth2UserInfo);

        // 4. OAuth2User로 반환
        return new PrincipalDetails(member, oAuth2UserAttributes);
    }

    private Member getOrSave(OAuth2UserInfo oAuth2UserInfo) {
        Member member = memberRepository.findByUserEmail(oAuth2UserInfo.getEmail())
            .orElse(null);

        if (member == null) {
            member = Member.builder()
                .userPw(passwordEncoder.encode(oAuth2UserInfo.getNickName()))
                .userId("kakao_" + oAuth2UserInfo.getId())
                .userEmail(oAuth2UserInfo.getEmail())
                .role(Role.ROLE_USER)
                .oAuthProvider(OAuthProvider.KAKAO)
                .build();

            return memberRepository.save(member);
        }

        return member;
    }

}
