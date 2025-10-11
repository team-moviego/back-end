package com.hwansol.moviego.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    ALREADY_RESERVED_SEAT(HttpStatus.BAD_REQUEST.value(), "이미 예약된 좌석입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN.value(), "권한이 없습니다."),
    FAIL_DELETE_BY_EXIST_MOVIE_SCHEDULE(HttpStatus.BAD_REQUEST.value(), "영화 스케줄이 존재하여 삭제할 수 없습니다."),
    FAIL_CREATE_MOVIE_SCHEDULE_BY_TIME(HttpStatus.BAD_REQUEST.value(), "해당 시간대에 영화 스케줄을 생성할 수 없습니다."),
    HARD_DELETE_FAIL(HttpStatus.BAD_REQUEST.value(), "영구 삭제에 실패하였습니다."),
    DUPLICATED(HttpStatus.BAD_REQUEST.value(), "이미 존재합니다."),
    READ_IMAGE_FAIL(HttpStatus.BAD_REQUEST.value(), "r2에서 이미지를 가져오는데 실패하였습니다."),
    ALREADY_DELETED(HttpStatus.BAD_REQUEST.value(), "이미 삭제되었습니다."),
    NOT_EXIST(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 엔티티입니다.");

    private final int status;
    private final String message;
}
