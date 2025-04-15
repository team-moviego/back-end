package com.hwansol.moviego.mail.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MailErrorCode {

    FAIL_SEND_MAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "메시지 전송 실패, 서버 관리자에게 문의하세요.");

    private final int status;
    private final String message;
}
