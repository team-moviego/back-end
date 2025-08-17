package com.hwansol.moviego.movie.model;

import com.hwansol.moviego.aop.EnumCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EnumCreator
public enum MovieRating {
    ALL("전체 관람가"),
    TWELVE("12세 관람가"),
    FIFTEEN("15세 관람가"),
    NINETEEN("19세 관람가");

    private final String description;
}
