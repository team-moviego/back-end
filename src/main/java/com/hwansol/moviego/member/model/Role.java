package com.hwansol.moviego.member.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ROLE_USER("USER", "회원"); // 일반 회원

    private final String name;
    private final String value;
}
