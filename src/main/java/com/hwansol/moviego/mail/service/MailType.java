package com.hwansol.moviego.mail.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailType {
    ID("아이디 찾기 메일"),
    PW("비밀번호 찾기 메일"),
    AUTH("인증번호 메일");

    private final String description;
}
