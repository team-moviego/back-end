package com.hwansol.moviego.common.exception;

public class DeleteException extends BusinessLogicException {

    public DeleteException(ErrorCode errorCode) {
        super(errorCode);
    }
}
