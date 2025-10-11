package com.hwansol.moviego.reservation.model;

import com.hwansol.moviego.aop.EnumCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EnumCreator
public enum ReservationType {
    RESERVATION("예약"),
    CANCEL("취소");

    private final String description;
}
