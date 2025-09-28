package com.hwansol.moviego.seat.dto;

import com.hwansol.moviego.seat.model.Seat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SeatSimpleGetDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;
        private String seatRow;
        private int seatNum;

        public Response(Long id, String seatRow, int seatNum) {
            boolean isValidateDataFail = id == null || id <= 0 || seatRow == null || seatRow.isBlank() || seatNum <= 0;

            if (isValidateDataFail) {
                throw new IllegalArgumentException("SeatSimpleGetDto.Response 생성 실패");
            }

            this.id = id;
            this.seatRow = seatRow;
            this.seatNum = seatNum;
        }

        public static SeatSimpleGetDto.Response from(Seat seat) {
            return Response.builder()
                    .id(seat.getId())
                    .seatRow(seat.getSeatRow())
                    .seatNum(seat.getSeatNum())
                    .build();
        }
    }
}
