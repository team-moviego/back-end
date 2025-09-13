package com.hwansol.moviego.common.exception;

public class NotFoundException extends BusinessLogicException {

    public NotFoundException() {
        super(ErrorCode.NOT_EXIST);
    }
}
