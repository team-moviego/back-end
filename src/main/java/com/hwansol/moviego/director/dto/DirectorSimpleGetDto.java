package com.hwansol.moviego.director.dto;

import com.hwansol.moviego.director.model.Director;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DirectorSimpleGetDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;
        private String name;

        public Response(Long id, String name) {
            boolean isValidateDataFail = id == null || id <= 0 || name == null || name.isBlank();

            if (isValidateDataFail) {
                throw new IllegalArgumentException("DirectorSimpleGetDto.Response 생성 실패");
            }

            this.id = id;
            this.name = name;
        }

        public static DirectorSimpleGetDto.Response from(Director director) {
            if (director == null) {
                throw new IllegalArgumentException("DirectorSimpleGetDto.Response 생성 실패");
            }

            return DirectorSimpleGetDto.Response.builder()
                    .id(director.getId())
                    .name(director.getName())
                    .build();
        }
    }
}
