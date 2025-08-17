package com.hwansol.moviego.common;

public class NotFoundException extends BusinessLogicException {

    public NotFoundException() {
        super(ErrorCode.NOT_EXIST);
    }
}
