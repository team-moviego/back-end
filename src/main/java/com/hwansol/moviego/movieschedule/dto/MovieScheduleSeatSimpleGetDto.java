package com.hwansol.moviego.movieschedule.dto;

import com.hwansol.moviego.movieschedule.model.MovieScheduleSeat;
import com.hwansol.moviego.movieschedule.model.SeatStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MovieScheduleSeatSimpleGetDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;
        private String seatRow;
        private int seatNum;
        private SeatStatus seatStatus;

        public Response(Long id, String seatRow, int seatNum, SeatStatus seatStatus) {
            boolean isValidateDataFail = id == null || id <= 0 || seatRow == null || seatRow.isBlank() || seatNum <= 0 || seatStatus == null;

            if (isValidateDataFail) {
                throw new IllegalArgumentException("MovieScheduleSeatSimpleGetDto.Response 생성 실패");
            }

            this.id = id;
            this.seatRow = seatRow;
            this.seatNum = seatNum;
            this.seatStatus = seatStatus;
        }

        public static MovieScheduleSeatSimpleGetDto.Response from(
                MovieScheduleSeat movieScheduleSeat) {
            return MovieScheduleSeatSimpleGetDto.Response.builder()
                    .id(movieScheduleSeat.getId())
                    .seatRow(movieScheduleSeat.getSeat().getSeatRow())
                    .seatNum(movieScheduleSeat.getSeat().getSeatNum())
                    .seatStatus(movieScheduleSeat.getSeatStatus())
                    .build();
        }
    }
}
