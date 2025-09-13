package com.hwansol.moviego.cookie.exception;

import lombok.Getter;

@Getter
public class CookieException extends RuntimeException {

    private final CookieErrorCode cookieErrorCode;

    public CookieException(CookieErrorCode cookieErrorCode) {
        super(cookieErrorCode.getMessage());
        this.cookieErrorCode = cookieErrorCode;
    }
}
