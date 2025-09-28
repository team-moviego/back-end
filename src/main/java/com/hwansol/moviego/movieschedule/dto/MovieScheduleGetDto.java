package com.hwansol.moviego.movieschedule.dto;

import com.hwansol.moviego.image.dto.ImageGetDto;
import com.hwansol.moviego.movie.dto.MovieGetDto;
import com.hwansol.moviego.movieschedule.model.MovieSchedule;
import com.hwansol.moviego.movieschedule.model.MovieScheduleSeat;
import com.hwansol.moviego.screen.dto.ScreenSimpleGetDto;
import java.time.LocalDateTime;
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
        private ScreenSimpleGetDto.Response screenInfo;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private List<MovieScheduleSeatSimpleGetDto.Response> seatInfoList;

        public Response(Long id, MovieGetDto.Response movieInfo,
                ScreenSimpleGetDto.Response screenInfo,
                LocalDateTime startDateTime, LocalDateTime endDateTime,
                List<MovieScheduleSeatSimpleGetDto.Response> seatInfoList) {
            boolean isValidateDataFail = id == null || id <= 0 || movieInfo == null || screenInfo == null || startDateTime == null || endDateTime == null || seatInfoList == null || seatInfoList.isEmpty();

            if (isValidateDataFail) {
                throw new IllegalArgumentException("MovieScheduleGetDto.Response 생성 실패");
            }

            this.id = id;
            this.movieInfo = movieInfo;
            this.screenInfo = screenInfo;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.seatInfoList = seatInfoList;
        }

        public static MovieScheduleGetDto.Response from(MovieSchedule movieSchedule,
                List<ImageGetDto.Response> movieImageList) {
            List<MovieScheduleSeat> movieScheduleSeats = movieSchedule.getMovieScheduleSeats();
            List<MovieScheduleSeatSimpleGetDto.Response> movieScheduleSeatList = movieScheduleSeats.stream()
                    .map(MovieScheduleSeatSimpleGetDto.Response::from)
                    .toList();

            return MovieScheduleGetDto.Response.builder()
                    .id(movieSchedule.getId())
                    .movieInfo(MovieGetDto.Response.from(movieSchedule.getMovie(), movieImageList))
                    .screenInfo(ScreenSimpleGetDto.Response.from(movieSchedule.getScreen()))
                    .startDateTime(movieSchedule.getStartDateTime())
                    .endDateTime(movieSchedule.getEndDateTime())
                    .seatInfoList(movieScheduleSeatList)
                    .build();
        }
    }
}
