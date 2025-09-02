package com.hwansol.moviego.common;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class CommonResponseDto {

    private Long id;

    public CommonResponseDto(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("CommonDto.Response 생성 실패");
        }

        this.id = id;
    }

    public static CommonResponseDto from(Long id) {
        return CommonResponseDto.builder()
                .id(id)
                .build();
    }
}
