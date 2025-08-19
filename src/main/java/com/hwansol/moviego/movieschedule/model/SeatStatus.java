package com.hwansol.moviego.movieschedule.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SeatStatus {
    AVAILABLE("예매 가능"),
    UNAVAILABLE("예매 불가능");

    private final String description;
}
