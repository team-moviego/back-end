package com.hwansol.moviego.screen.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ScreenDeleteDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotBlank(message = "영구 삭제를 위한 문구를 입력하지 않았습니다.")
        private String deleteString;

        public Request(String deleteString) {
            if (deleteString == null || deleteString.isBlank()) {
                throw new IllegalArgumentException("ScreenDeleteDto.Request 생성 실패");
            }

            this.deleteString = deleteString;
        }
    }
}
