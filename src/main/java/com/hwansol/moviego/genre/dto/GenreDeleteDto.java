package com.hwansol.moviego.genre.dto;

import com.hwansol.moviego.genre.model.Genre;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GenreDeleteDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;

        public Response(Long id) {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("GenreDeleteDto.Response 생성 실패");

            }
            this.id = id;
        }

        public static GenreDeleteDto.Response from(Genre genre) {
            return Response.builder()
                    .id(genre.getId())
                    .build();
        }
    }
}
