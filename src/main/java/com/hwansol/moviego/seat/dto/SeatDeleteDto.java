package com.hwansol.moviego.seat.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SeatDeleteDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        private String deleteString;

        public Request(String deleteString) {
            if (deleteString == null || deleteString.isBlank()) {
                throw new IllegalArgumentException("SeatDeleteDto.Request 생성 실패");
            }

            this.deleteString = deleteString;
        }
    }
}
