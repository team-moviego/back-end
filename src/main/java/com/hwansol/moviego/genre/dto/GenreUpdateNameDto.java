package com.hwansol.moviego.genre.dto;

import com.hwansol.moviego.genre.model.Genre;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GenreUpdateNameDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        private String newName;

        public Request(String newName) {
            if (newName == null || newName.isBlank()) {
                throw new IllegalArgumentException("GenreUpdateNameDto.Request 생성 실패");
            }

            this.newName = newName;
        }

        public Genre toEntity() {
            return Genre.builder()
                    .name(this.newName)
                    .build();
        }
    }
}
