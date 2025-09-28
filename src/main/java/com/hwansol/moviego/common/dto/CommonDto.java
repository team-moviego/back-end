package com.hwansol.moviego.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommonDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class DeleteRequest {

        @NotBlank(message = "영구 삭제를 위한 문구를 입력해주세요.")
        private String deleteString;

        public DeleteRequest(String deleteString) {
            if (deleteString == null || deleteString.isBlank()) {
                throw new IllegalArgumentException("CommonDto.DeleteRequest 생성 실패");
            }

            this.deleteString = deleteString;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;

        public Response(Long id) {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("CommonDto.Response 생성 실패");
            }

            this.id = id;
        }

        public static CommonDto.Response from(Long id) {
            return CommonDto.Response.builder()
                    .id(id)
                    .build();
        }
    }
}
