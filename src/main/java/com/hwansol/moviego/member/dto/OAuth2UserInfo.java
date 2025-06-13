package com.hwansol.moviego.member.dto;

import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class OAuth2UserInfo {

    private String id;
    private String nickName;
    private String email;

    @SuppressWarnings("unchecked")
    public static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2UserInfo.builder()
            .id(String.valueOf(attributes.get("id")))
            .nickName(String.valueOf(profile.get("nickname")))
            .email(String.valueOf(account.get("email")))
            .build();
    }
}
