package com.hwansol.moviego.movieschedule.model;

import com.hwansol.moviego.reservation.model.Reservation;
import com.hwansol.moviego.seat.model.Seat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieScheduleSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus seatStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_schedule_id", nullable = false)
    private MovieSchedule movieSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Builder
    public MovieScheduleSeat(SeatStatus seatStatus) {
        if (seatStatus == null) {
            throw new IllegalArgumentException("MovieScheduleSeat 엔티티 생성 실패");
        }

        this.seatStatus = seatStatus;
    }

    public void relatedMovieSchedule(MovieSchedule movieSchedule) {
        if (this.movieSchedule != null) {
            throw new IllegalStateException("이미 연결된 상태입니다.");
        }

        if (movieSchedule == null) {
            throw new IllegalArgumentException("연관관계 연결 실패");
        }

        this.movieSchedule = movieSchedule;
    }

    public void relatedSeat(Seat seat) {
        if (this.seat != null) {
            throw new IllegalStateException("이미 연결된 상태입니다.");
        }

        if (seat == null) {
            throw new IllegalArgumentException("연관관계 연결 실패");
        }

        this.seat = seat;
    }

    public void relatedReservation(Reservation reservation) {
        if (this.reservation != null) {
            throw new IllegalStateException("이미 연결된 상태입니다.");
        }

        if (reservation == null) {
            throw new IllegalArgumentException("연관관계 연결 실패");
        }

        this.reservation = reservation;
    }

    // 좌석 예약 처리
    public void reserveSeat() {
        if (this.seatStatus.equals(SeatStatus.UNAVAILABLE)) {
            throw new IllegalStateException("이미 예약처리된 좌석입니다.");
        }

        this.seatStatus = SeatStatus.UNAVAILABLE;
    }
}
