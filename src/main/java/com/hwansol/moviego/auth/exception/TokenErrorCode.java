package com.hwansol.moviego.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TokenErrorCode {
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST.value(), "토큰이 만료되었습니다.");

    private final int status;
    private final String message;
}
