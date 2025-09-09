package com.hwansol.moviego.screen.dto;

import com.hwansol.moviego.movieschedule.dto.MovieScheduleListGetDto;
import com.hwansol.moviego.screen.model.Screen;
import com.hwansol.moviego.seat.dto.SeatSimpleGetDto;
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
        private List<SeatSimpleGetDto.Response> seatList;
        private List<MovieScheduleListGetDto.Response> movieScheduleList;

        public Response(Long id, String name, List<SeatSimpleGetDto.Response> seatList,
                List<MovieScheduleListGetDto.Response> movieScheduleList) {
            boolean isValidateDataFail = id == null || id <= 0 || name == null || name.isBlank();

            if (isValidateDataFail) {
                throw new IllegalArgumentException("ScreenGetDto.Response 생성 실패");
            }

            this.id = id;
            this.name = name;
            this.seatList = seatList;
            this.movieScheduleList = movieScheduleList;
        }

        public static ScreenGetDto.Response from(Screen screen,
                List<SeatSimpleGetDto.Response> seatList,
                List<MovieScheduleListGetDto.Response> movieScheduleResponseList) {
            return Response.builder()
                    .id(screen.getId())
                    .name(screen.getName())
                    .seatList(seatList)
                    .movieScheduleList(movieScheduleResponseList)
                    .build();
        }
    }
}
