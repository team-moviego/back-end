package com.hwansol.moviego.genre.dto;

import com.hwansol.moviego.genre.model.Genre;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GenreCreateDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotBlank(message = "장르 이름을 입력해주세요.")
        private String name;

        public Request(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("GenreCreateDto.Request 생성 실패");
            }

            this.name = name;
        }

        public Genre toEntity() {
            return Genre.builder()
                    .name(this.name)
                    .build();
        }
    }
}
