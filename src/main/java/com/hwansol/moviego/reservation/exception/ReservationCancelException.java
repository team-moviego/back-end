package com.hwansol.moviego.reservation.exception;

import com.hwansol.moviego.common.exception.BusinessLogicException;
import com.hwansol.moviego.common.exception.ErrorCode;

public class ReservationCancelException extends BusinessLogicException {

    public ReservationCancelException(ErrorCode errorCode) {
        super(errorCode);
    }
}
