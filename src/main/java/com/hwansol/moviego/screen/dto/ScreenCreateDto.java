package com.hwansol.moviego.screen.dto;

import com.hwansol.moviego.screen.model.Screen;
import com.hwansol.moviego.seat.dto.SeatCreateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ScreenCreateDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotBlank(message = "상영관 이름을 입력해주세요.")
        private String name;

        @Valid
        @NotEmpty(message = "좌석 생성 정보를 입력해주세요.")
        private List<SeatCreateDto.Request> seatCreateDtoList;

        public Request(String name, List<SeatCreateDto.Request> seatCreateDtoList) {
            boolean isValidateDataFail = name == null || name.isBlank() || seatCreateDtoList == null || seatCreateDtoList.isEmpty();

            if (isValidateDataFail) {
                throw new IllegalArgumentException("ScreenCreateDto.Request 생성 실패");
            }

            this.name = name;
            this.seatCreateDtoList = seatCreateDtoList;
        }

        public Screen toEntity() {
            return Screen.builder()
                    .name(this.name)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;

        public Response(Long id) {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ScreenCreateDto.Response 생성 실패");
            }

            this.id = id;
        }

        public static ScreenCreateDto.Response from(Screen screen) {
            return ScreenCreateDto.Response.builder()
                    .id(screen.getId())
                    .build();
        }
    }
}
