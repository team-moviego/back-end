package com.hwansol.moviego.cookie.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CookieErrorCode {
    NOT_FOUND_COOKIE(HttpStatus.BAD_REQUEST.value(), "저장된 쿠키가 존재하지 않습니다.");

    private final int status;
    private final String message;
}
