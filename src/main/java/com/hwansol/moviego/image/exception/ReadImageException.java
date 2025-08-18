package com.hwansol.moviego.image.exception;

import com.hwansol.moviego.common.BusinessLogicException;
import com.hwansol.moviego.common.ErrorCode;

public class ReadImageException extends BusinessLogicException {

    public ReadImageException() {
        super(ErrorCode.READ_IMAGE_FAIL);
    }
}
