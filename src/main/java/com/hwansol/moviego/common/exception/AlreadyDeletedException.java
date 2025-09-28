package com.hwansol.moviego.common.exception;

public class AlreadyDeletedException extends BusinessLogicException {

    public AlreadyDeletedException() {
        super(ErrorCode.ALREADY_DELETED);
    }
}
