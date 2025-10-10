package com.hwansol.moviego.reservation.model;

import com.hwansol.moviego.aop.EnumCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EnumCreator
public enum PayType {
    PAY("결제"),
    CANCEL("환불");

    private final String description;
}
