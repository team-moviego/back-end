package com.hwansol.moviego.genre.dto;

import com.hwansol.moviego.genre.model.Genre;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GenreSimpleGetDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        public Long id;
        public String name;

        public Response(Long id, String name) {
            boolean isValidateDataFail = id == null || id <= 0 || name == null || name.isBlank();

            if (isValidateDataFail) {
                throw new IllegalArgumentException("GenreGetDto.Response 생성 실패");
            }

            this.id = id;
            this.name = name;
        }

        public static GenreSimpleGetDto.Response from(Genre genre) {
            return Response.builder()
                    .id(genre.getId())
                    .name(genre.getName())
                    .build();
        }
    }
}
