package com.hwansol.moviego.common;

public class AlreadyDeletedException extends BusinessLogicException {

    public AlreadyDeletedException() {
        super(ErrorCode.ALREADY_DELETED);
    }
}
