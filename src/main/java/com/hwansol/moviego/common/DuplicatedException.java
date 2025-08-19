package com.hwansol.moviego.common;

public class DuplicatedException extends BusinessLogicException {

    public DuplicatedException() {
        super(ErrorCode.DUPLICATED);
    }
}
