package com.hwansol.moviego.genre.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GenreUpdateNameDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotBlank(message = "변경할 장르명을 입력해주세요.")
        private String newName;

        public Request(String newName) {
            if (newName == null || newName.isBlank()) {
                throw new IllegalArgumentException("GenreUpdateNameDto.Request 생성 실패");
            }

            this.newName = newName;
        }
    }
}
