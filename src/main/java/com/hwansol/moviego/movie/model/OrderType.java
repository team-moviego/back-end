package com.hwansol.moviego.movie.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderType {
    DATE("개봉일순"),
    GRADE("평점순"),
    RATE("예매율순");

    private final String description;
}
