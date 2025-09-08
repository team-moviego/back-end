package com.hwansol.moviego.image.exception;

import com.hwansol.moviego.common.exception.BusinessLogicException;
import com.hwansol.moviego.common.exception.ErrorCode;

public class ReadImageException extends BusinessLogicException {

    public ReadImageException() {
        super(ErrorCode.READ_IMAGE_FAIL);
    }
}
