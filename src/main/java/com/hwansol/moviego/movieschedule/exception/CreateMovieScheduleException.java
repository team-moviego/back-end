package com.hwansol.moviego.movieschedule.exception;

import com.hwansol.moviego.common.exception.BusinessLogicException;
import com.hwansol.moviego.common.exception.ErrorCode;

public class CreateMovieScheduleException extends BusinessLogicException {

    public CreateMovieScheduleException(ErrorCode errorCode) {
        super(errorCode);
    }
}
