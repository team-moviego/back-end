package com.hwansol.moviego.screen.dto;

import com.hwansol.moviego.movieschedule.dto.MovieScheduleGetDto;
import com.hwansol.moviego.screen.model.Screen;
import com.hwansol.moviego.seat.dto.SeatGetDto;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ScreenGetDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;
        private String name;
        private List<SeatGetDto.Response> seatList;
        private List<MovieScheduleGetDto.Response> movieScheduleList;

        public Response(Long id, String name, List<SeatGetDto.Response> seatList, List<MovieScheduleGetDto.Response> movieScheduleList) {
            boolean isValidateDataFail = id == null || id <= 0 || name == null || name.isBlank();

            if (isValidateDataFail) {
                throw new IllegalArgumentException("ScreenGetDto.Response 생성 실패");
            }

            this.id = id;
            this.name = name;
            this.seatList = seatList;
            this.movieScheduleList = movieScheduleList;
        }

        public static ScreenGetDto.Response from(Screen screen, List<MovieScheduleGetDto.Response> movieScheduleResponseList) {
            List<SeatGetDto.Response> seatList = screen.getSeats() == null ? new ArrayList<>() : screen.getSeats().stream()
                    .map(SeatGetDto.Response::from)
                    .toList();

            return Response.builder()
                    .id(screen.getId())
                    .name(screen.getName())
                    .seatList(seatList)
                    .movieScheduleList(movieScheduleResponseList)
                    .build();
        }
    }
}
