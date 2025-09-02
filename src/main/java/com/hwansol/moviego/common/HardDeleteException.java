package com.hwansol.moviego.common;

public class HardDeleteException extends BusinessLogicException {

    public HardDeleteException() {
        super(ErrorCode.HARD_DELETE_FAIL);
    }
}
