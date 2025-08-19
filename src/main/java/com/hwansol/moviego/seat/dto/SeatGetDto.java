package com.hwansol.moviego.seat.dto;

import com.hwansol.moviego.seat.model.Seat;
import com.hwansol.moviego.seat.model.SeatType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SeatGetDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;
        private int seatNum;
        private SeatType seatType;

        public Response(Long id, int seatNum, SeatType seatType) {
            boolean isValidateDataFail = id == null || id <= 0 || seatNum <= 0 || seatType == null;

            if (isValidateDataFail) {
                throw new IllegalArgumentException("SeatGetDto.Response 생성 실패");
            }

            this.id = id;
            this.seatNum = seatNum;
            this.seatType = seatType;
        }

        public static SeatGetDto.Response from(Seat seat) {
            return SeatGetDto.Response.builder()
                    .id(seat.getId())
                    .seatNum(seat.getSeatNum())
                    .seatType(seat.getSeatType())
                    .build();
        }
    }
}
