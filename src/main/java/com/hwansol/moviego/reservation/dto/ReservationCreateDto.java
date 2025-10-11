package com.hwansol.moviego.reservation.dto;

import com.hwansol.moviego.reservation.model.PayType;
import com.hwansol.moviego.reservation.model.Reservation;
import com.hwansol.moviego.validation.IsEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReservationCreateDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotNull(message = "가격을 입력해주세요.")
        @Positive(message = "가격은 0 또는 음수일 수 없습니다.")
        private BigDecimal payment;

        @NotNull(message = "결제 타입을 입력해주세요.")
        @IsEnum(message = "올바른 enum값을 입력해주세요.")
        private PayType payType;

        @NotNull(message = "좌석 정보를 입력해주세요.")
        private List<@NotNull(message = "좌석 pk를 입력해주세요.") @Positive(message = "pk는 0 또는 음수일 수 없습니다.") Long> movieScheduleSeatIds;

        @NotNull(message = "영화 스케줄 pk를 입력해주세요.")
        @Positive(message = "pk는 0 또는 음수일 수 없습니다.")
        private Long movieScheduleId;

        public Request(BigDecimal payment, PayType payType, List<Long> movieScheduleSeatIds, Long movieScheduleId) {
            boolean isValidatedFail = payment == null || payment.compareTo(BigDecimal.ZERO) <= 0 || payType == null || movieScheduleSeatIds == null || movieScheduleSeatIds.isEmpty() || movieScheduleId == null || movieScheduleId <= 0;

            if (isValidatedFail) {
                throw new IllegalArgumentException("ReservationCreateDto.Request 생성 실패");
            }

            this.payment = payment;
            this.payType = payType;
            this.movieScheduleSeatIds = movieScheduleSeatIds;
            this.movieScheduleId = movieScheduleId;
        }

        public Reservation toEntity(String reservationNum) {
            return Reservation.builder()
                    .reservationNum(reservationNum)
                    .payment(this.payment)
                    .payType(this.payType)
                    .build();
        }
    }
}
