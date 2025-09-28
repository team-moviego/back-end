package com.hwansol.moviego.screen.dto;

import com.hwansol.moviego.screen.model.Screen;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ScreenSimpleGetDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;
        private String name;

        public Response(Long id, String name) {
            boolean isValidDataFail = id == null || id <= 0 || name == null || name.isBlank();

            if (isValidDataFail) {
                throw new IllegalArgumentException("ScreenSimpleGetDto.Response 생성 실패");
            }

            this.id = id;
            this.name = name;
        }

        public static ScreenSimpleGetDto.Response from(Screen screen) {
            return ScreenSimpleGetDto.Response.builder()
                    .id(screen.getId())
                    .name(screen.getName())
                    .build();
        }
    }
}
