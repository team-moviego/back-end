package com.hwansol.moviego.seat.dto;

import com.hwansol.moviego.seat.model.Seat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SeatCreateDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        private String seatRow;
        private int seatNum;
        private Long screenId;

        public Request(String seatRow, int seatNum, Long screenId) {
            boolean isValidateDataFail = seatRow == null || seatRow.isBlank() || seatNum <= 0 || screenId == null || screenId <= 0;

            if (isValidateDataFail) {
                throw new IllegalArgumentException("SeatCreateDto.Request 생성 실패");
            }

            this.seatRow = seatRow;
            this.seatNum = seatNum;
            this.screenId = screenId;
        }

        public Seat toEntity() {
            return Seat.builder()
                    .seatNum(this.seatNum)
                    .seatRow(this.seatRow)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;

        public Response(Long id) {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("SeatCreateDto.Response 생성 실패");
            }

            this.id = id;
        }

        public static SeatCreateDto.Response from(Seat seat) {
            return SeatCreateDto.Response.builder()
                    .id(seat.getId())
                    .build();
        }
    }
}
