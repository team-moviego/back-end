package com.hwansol.moviego.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ALREADY_DELETED(HttpStatus.BAD_REQUEST.value(), "이미 삭제되었습니다."),
    NOT_EXIST(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 엔티티입니다.");

    private final int status;
    private final String message;
}
