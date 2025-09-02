package com.hwansol.moviego.seat.dto;

import com.hwansol.moviego.seat.model.Seat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SeatCreateDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotBlank(message = "좌석 열을 입력해주세요.")
        private String seatRow;

        @Positive(message = "좌석번호는 0 또는 음수일 수 없습니다.")
        private int seatNum;

        @Positive(message = "pk는 0 또는 음수일 수 없습니다.")
        private Long screenId;

        public Request(String seatRow, int seatNum, Long screenId) {
            boolean isValidateDataFail = seatRow == null || seatRow.isBlank() || seatNum <= 0 || screenId <= 0;

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
}
