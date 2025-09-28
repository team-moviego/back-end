package com.hwansol.moviego.common.exception;

public class DuplicatedException extends BusinessLogicException {

    public DuplicatedException() {
        super(ErrorCode.DUPLICATED);
    }
}
