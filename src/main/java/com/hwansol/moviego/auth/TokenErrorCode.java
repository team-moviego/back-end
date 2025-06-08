package com.hwansol.moviego.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TokenErrorCode {

    NOT_FOUND_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(), "refreshToken이 존재하지 않습니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.FORBIDDEN.value(), "accessToken이 만료되었습니다. 재발급 진행해주세요."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(), "refreshToken이 만료되었습니다. 재로그인을 진행해주세요.");

    private final int status;
    private final String message;
}
