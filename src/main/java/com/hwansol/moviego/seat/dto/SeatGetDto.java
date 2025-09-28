package com.hwansol.moviego.seat.dto;

import com.hwansol.moviego.screen.dto.ScreenSimpleGetDto;
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
        private String seatRow;
        private int seatNum;
        private ScreenSimpleGetDto.Response screenInfo;

        public Response(Long id, String seatRow, int seatNum,
                ScreenSimpleGetDto.Response screenInfo) {
            boolean isValidateDataFail = id == null || id <= 0 || seatRow == null || seatRow.isBlank() || seatNum <= 0 || screenInfo == null;

            if (isValidateDataFail) {
                throw new IllegalArgumentException("SeatGetDto.Response 생성 실패");
            }

            this.id = id;
            this.seatRow = seatRow;
            this.seatNum = seatNum;
            this.screenInfo = screenInfo;
        }

        public static SeatGetDto.Response from(Seat seat) {
            ScreenSimpleGetDto.Response screenInfo = ScreenSimpleGetDto.Response.from(
                    seat.getScreen());

            return SeatGetDto.Response.builder()
                    .id(seat.getId())
                    .seatRow(seat.getSeatRow())
                    .seatNum(seat.getSeatNum())
                    .screenInfo(screenInfo)
                    .build();
        }
    }
}
