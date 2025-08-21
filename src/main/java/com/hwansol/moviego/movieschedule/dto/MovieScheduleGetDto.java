package com.hwansol.moviego.movieschedule.dto;

import com.hwansol.moviego.image.dto.ImageGetDto;
import com.hwansol.moviego.movie.dto.MovieGetDto;
import com.hwansol.moviego.movieschedule.model.MovieSchedule;
import com.hwansol.moviego.movieschedule.model.MovieScheduleSeat;
import com.hwansol.moviego.movieschedule.model.SeatStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MovieScheduleGetDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;
        private MovieGetDto.Response movieInfo;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private String screenName;
        private int totalSeat;
        private int availableSeat;

        public Response(Long id, MovieGetDto.Response movieInfo, LocalDateTime startDateTime, LocalDateTime endDateTime, String screenName, int totalSeat, int availableSeat) {
            boolean isValidateDataFail = id == null || id <= 0 || movieInfo == null || startDateTime == null || endDateTime == null || screenName == null || screenName.isBlank() || totalSeat <= 0 || availableSeat < 0;

            if (isValidateDataFail) {
                throw new IllegalArgumentException("MovieScheduleGetDto.Response 생성 실패");
            }

            this.id = id;
            this.movieInfo = movieInfo;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.screenName = screenName;
            this.totalSeat = totalSeat;
            this.availableSeat = availableSeat;
        }

        public static MovieScheduleGetDto.Response from(MovieSchedule movieSchedule, List<ImageGetDto.Response> imageList) {
            List<MovieScheduleSeat> availableSeats = movieSchedule.getMovieScheduleSeats() == null ? new ArrayList<>() :
                    movieSchedule.getMovieScheduleSeats().stream()
                            .filter(s -> s.getSeatStatus().equals(SeatStatus.AVAILABLE))
                            .toList();

            return Response.builder()
                    .id(movieSchedule.getId())
                    .movieInfo(MovieGetDto.Response.from(movieSchedule.getMovie(), imageList))
                    .startDateTime(movieSchedule.getStartDateTime())
                    .endDateTime(movieSchedule.getEndDateTime())
                    .screenName(movieSchedule.getScreen().getName())
                    .totalSeat(movieSchedule.getMovieScheduleSeats().size())
                    .availableSeat(availableSeats.size())
                    .build();
        }
    }
}
