package com.hwansol.moviego.movieschedule.dto;

import com.hwansol.moviego.movieschedule.model.MovieSchedule;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MovieScheduleCreateDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotNull(message = "영화 pk를 입력해주세요.")
        @Positive(message = "pk는 0 또는 음수일 수 없습니다.")
        private Long movieId;

        @NotNull(message = "상영관 pk를 입력해주세요.")
        @Positive(message = "pk는 0 또는 음수일 수 없습니다.")
        private Long screenId;

        @NotNull(message = "영화 시작 시간을 입력해주세요.")
        @FutureOrPresent(message = "영화 시작 시간은 현재 또는 미래의 시간대여야 합니다.")
        private LocalDateTime startDateTime;

        @NotNull(message = "영화 종료 시간을 입력해주세요.")
        @Future(message = "영화 종료 시간은 미래의 시간대여야 합니다.")
        private LocalDateTime endDateTime;

        public Request(Long movieId, Long screenId, LocalDateTime startDateTime,
                LocalDateTime endDateTime) {
            LocalDateTime now = LocalDateTime.now();
            boolean isValidateDataFail = movieId == null || movieId <= 0 || screenId == null || screenId <= 0 || startDateTime == null || startDateTime.isBefore(
                    now) || endDateTime == null || endDateTime.isEqual(
                    startDateTime) || endDateTime.isBefore(startDateTime);

            if (isValidateDataFail) {
                throw new IllegalArgumentException("MovieScheduleCreateDto.Request 생성 실패");
            }

            this.movieId = movieId;
            this.screenId = screenId;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
        }

        public MovieSchedule toEntity() {
            return MovieSchedule.builder()
                    .startDateTime(this.startDateTime)
                    .endDateTime(this.endDateTime)
                    .build();
        }
    }
}
