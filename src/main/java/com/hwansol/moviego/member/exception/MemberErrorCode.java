package com.hwansol.moviego.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode {
    SOCIAL_USER(HttpStatus.BAD_REQUEST.value(), "카카오 소셜 회원 가입된 회원입니다."),
    WRONG_ORIGIN_PW(HttpStatus.BAD_REQUEST.value(), "현재 아이디의 비밀번호가 아닙니다."),
    DIFF_PW_AND_CONFIRM(HttpStatus.BAD_REQUEST.value(), "입력한 비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    NOT_COMPLETED_AUTH(HttpStatus.BAD_REQUEST.value(), "이메일 인증을 먼저 진행해주세요."),
    ORIGIN_EQUALS_NEW_OF_EMAIL(HttpStatus.BAD_REQUEST.value(), "변경할 이메일 주소가 기존 이메일 주소와 같습니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST.value(), "올바르지 않는 비밀번호입니다."),
    TIME_OVER_AUTH(HttpStatus.BAD_REQUEST.value(), "인증번호 입력 시간이 초과되었습니다."),
    WRONG_AUTH_NUM(HttpStatus.BAD_REQUEST.value(), "올바른 인증번호가 아닙니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST.value(), "이미 사용중인 이메일입니다."),
    DUPLICATED_ID(HttpStatus.BAD_REQUEST.value(), "이미 사용중인 아이디입니다."),
    NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 회원입니다.");

    private final int status;
    private final String message;
}
