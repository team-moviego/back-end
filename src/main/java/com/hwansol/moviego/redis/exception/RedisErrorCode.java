package com.hwansol.moviego.redis.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RedisErrorCode {
    NOT_EXIST_AUTH(HttpStatus.BAD_REQUEST.value(), "이메일 인증을 진행하지 않았습니다."),
    NOT_EXIST_REFRESH_TOKEN(HttpStatus.BAD_REQUEST.value(), "레디스에 저장된 리프레시 토큰이 없습니다.");

    private final int status;
    private final String message;
}
