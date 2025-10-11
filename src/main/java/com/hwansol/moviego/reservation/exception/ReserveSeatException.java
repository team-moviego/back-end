package com.hwansol.moviego.reservation.exception;

import com.hwansol.moviego.common.exception.BusinessLogicException;
import com.hwansol.moviego.common.exception.ErrorCode;

public class ReserveSeatException extends BusinessLogicException {

    public ReserveSeatException(ErrorCode errorCode) {
        super(errorCode);
    }
}
