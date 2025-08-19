package com.hwansol.moviego.seat.dto;

import com.hwansol.moviego.movieschedule.model.SeatStatus;
import com.hwansol.moviego.seat.model.Seat;
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
        private SeatStatus seatStatus;

        public Response(Long id, int seatNum, SeatStatus seatStatus) {
            boolean isValidateDataFail = id == null || id <= 0 || seatNum <= 0 || seatStatus == null;

            if (isValidateDataFail) {
                throw new IllegalArgumentException("SeatGetDto.Response 생성 실패");
            }

            this.id = id;
            this.seatNum = seatNum;
            this.seatStatus = seatStatus;
        }

        public static SeatGetDto.Response from(Seat seat) {
            return SeatGetDto.Response.builder()
                    .id(seat.getId())
                    .seatNum(seat.getSeatNum())
                    .seatStatus(seat.getSeatStatus())
                    .build();
        }
    }
}
