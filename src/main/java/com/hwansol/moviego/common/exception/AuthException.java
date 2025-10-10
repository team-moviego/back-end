package com.hwansol.moviego.common.exception;

public class AuthException extends BusinessLogicException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
