package com.hwansol.moviego.common;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class CommonDto {

    private Long id;

    public CommonDto(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("CommonDto.Response 생성 실패");
        }

        this.id = id;
    }

    public static CommonDto from(Long id) {
        return CommonDto.builder()
                .id(id)
                .build();
    }
}
