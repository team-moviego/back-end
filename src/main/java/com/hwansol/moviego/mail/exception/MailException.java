package com.hwansol.moviego.mail.exception;

import lombok.Getter;

@Getter
public class MailException extends RuntimeException {

    private final MailErrorCode mailErrorCode;

    public MailException(MailErrorCode mailErrorCode) {
        super(mailErrorCode.getMessage());
        this.mailErrorCode = mailErrorCode;
    }
}
