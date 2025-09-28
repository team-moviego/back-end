package com.hwansol.moviego.director.dto;

import com.hwansol.moviego.director.model.Director;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DirectorCreateDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotBlank(message = "감독 이름을 입력해주세요.")
        private String name;

        public Request(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("DirectorCreateDto.Request 생성 실패");
            }

            this.name = name;
        }

        public Director toEntity() {
            return Director.builder()
                    .name(this.name)
                    .build();
        }
    }
}
