package com.hwansol.moviego.reservation.dto;

import com.hwansol.moviego.movie.model.Movie;
import com.hwansol.moviego.movie.model.MovieRating;
import com.hwansol.moviego.movieschedule.dto.MovieScheduleSeatSimpleGetDto;
import com.hwansol.moviego.movieschedule.model.MovieSchedule;
import com.hwansol.moviego.movieschedule.model.MovieScheduleSeat;
import com.hwansol.moviego.reservation.model.PayType;
import com.hwansol.moviego.reservation.model.Reservation;
import com.hwansol.moviego.screen.model.Screen;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReservationGetDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;
        private String reservationNum;
        private BigDecimal payment;
        private PayType payType;
        private String movieNameKo;
        private String movieNameEn;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private MovieRating movieRating;
        private String screenName;
        private List<MovieScheduleSeatSimpleGetDto.Response> seats;
        private LocalDateTime createdAt;

        public Response(Long id, String reservationNum, BigDecimal payment, PayType payType, String movieNameKo, String movieNameEn, LocalDateTime startAt, LocalDateTime endAt, MovieRating movieRating, String screenName, List<MovieScheduleSeatSimpleGetDto.Response> seats, LocalDateTime createdAt) {
            boolean isValidatedFail = id == null || id <= 0 || reservationNum == null || reservationNum.isBlank() || payment == null || payType == null || movieNameKo == null || movieNameKo.isBlank() || movieNameEn == null || movieNameEn.isBlank() || startAt == null || endAt == null || movieRating == null || screenName == null || screenName.isBlank() || seats == null || seats.isEmpty() || createdAt == null;

            if (isValidatedFail) {
                throw new IllegalArgumentException("ReservationGetDto.Response 생성 실패");
            }

            this.id = id;
            this.reservationNum = reservationNum;
            this.payment = payment;
            this.payType = payType;
            this.movieNameKo = movieNameKo;
            this.movieNameEn = movieNameEn;
            this.startAt = startAt;
            this.endAt = endAt;
            this.movieRating = movieRating;
            this.screenName = screenName;
            this.seats = seats;
            this.createdAt = createdAt;
        }

        public static ReservationGetDto.Response from(Reservation reservation) {
            if (reservation == null) {
                throw new IllegalArgumentException("ReservationGetDto.Response 생성 실패");
            }

            Movie movie = reservation.getMovieSchedule().getMovie();
            MovieSchedule movieSchedule = reservation.getMovieSchedule();
            Screen screen = movieSchedule.getScreen();
            List<MovieScheduleSeat> movieScheduleSeats = movieSchedule.getMovieScheduleSeats();
            List<MovieScheduleSeatSimpleGetDto.Response> seats = movieScheduleSeats != null && !movieScheduleSeats.isEmpty() ? movieScheduleSeats.stream()
                    .map(MovieScheduleSeatSimpleGetDto.Response::from)
                    .toList() : null;

            if (seats == null || seats.isEmpty()) {
                throw new IllegalStateException("ReservationGetDto.Response 생성 실패");
            }

            return ReservationGetDto.Response.builder()
                    .id(reservation.getId())
                    .reservationNum(reservation.getReservationNum())
                    .payment(reservation.getPayment())
                    .payType(reservation.getPayType())
                    .movieNameKo(movie.getTitleKo())
                    .movieNameEn(movie.getTitleEn())
                    .startAt(movieSchedule.getStartDateTime())
                    .endAt(movieSchedule.getEndDateTime())
                    .movieRating(movie.getRating())
                    .screenName(screen.getName())
                    .seats(seats)
                    .createdAt(reservation.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class SimpleResponse {

        private Long id;
        private String reservationNum;
        private BigDecimal payment;
        private PayType payType;
        private String movieNameKo;
        private String movieNameEn;
        private LocalDateTime createdAt;

        public SimpleResponse(Long id, String reservationNum, BigDecimal payment, PayType payType, String movieNameKo, String movieNameEn, LocalDateTime createdAt) {
            boolean isValidatedFail = id == null || id <= 0 || reservationNum == null || reservationNum.isBlank() || payment == null || payment.compareTo(BigDecimal.ZERO) <= 0 || payType == null || movieNameKo == null || movieNameKo.isBlank() || movieNameEn == null || movieNameEn.isBlank() || createdAt == null;

            if (isValidatedFail) {
                throw new IllegalArgumentException("ReservationGetDto.SimpleResponse 생성 실패");
            }

            this.id = id;
            this.reservationNum = reservationNum;
            this.payment = payment;
            this.payType = payType;
            this.movieNameKo = movieNameKo;
            this.movieNameEn = movieNameEn;
            this.createdAt = createdAt;
        }

        public static ReservationGetDto.SimpleResponse from(Reservation reservation) {
            if (reservation == null) {
                throw new IllegalArgumentException("ReservationGetDto.SimpleResponse 생성 실패");
            }

            Movie movie = reservation.getMovieSchedule().getMovie();

            return ReservationGetDto.SimpleResponse.builder()
                    .id(reservation.getId())
                    .reservationNum(reservation.getReservationNum())
                    .payment(reservation.getPayment())
                    .payType(reservation.getPayType())
                    .movieNameKo(movie.getTitleKo())
                    .movieNameEn(movie.getTitleEn())
                    .createdAt(reservation.getCreatedAt())
                    .build();
        }
    }
}
