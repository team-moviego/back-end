package com.hwansol.moviego.director.dto;

import com.hwansol.moviego.director.model.Director;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DirectorUpdateNameDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotBlank(message = "변경할 감독명을 입력해주세요.")
        private String newName;

        public Request(String newName) {
            if (newName == null || newName.isBlank()) {
                throw new IllegalArgumentException("DirectorUpdateName.Request 생성 실패");
            }

            this.newName = newName;
        }

        public Director toEntity() {
            return Director.builder()
                    .name(this.newName)
                    .build();
        }
    }
}
