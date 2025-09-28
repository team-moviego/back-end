package com.hwansol.moviego.image.model;

import com.hwansol.moviego.aop.EnumCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EnumCreator
public enum PosterType {
    MAIN("메인 포스터"),
    NORMAL("일반 포스터");

    private final String description;
}
