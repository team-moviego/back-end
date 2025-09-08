package com.hwansol.moviego.common.exception;

public class HardDeleteException extends BusinessLogicException {

    public HardDeleteException() {
        super(ErrorCode.HARD_DELETE_FAIL);
    }
}
