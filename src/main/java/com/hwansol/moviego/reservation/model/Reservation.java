package com.hwansol.moviego.reservation.model;

import com.hwansol.moviego.config.BaseTImeEntity;
import com.hwansol.moviego.member.model.Member;
import com.hwansol.moviego.movieschedule.model.MovieSchedule;
import com.hwansol.moviego.movieschedule.model.MovieScheduleSeat;
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
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class Reservation extends BaseTImeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reservationNum;

    @Column(nullable = false)
    private BigDecimal payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationType reservationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_schedule_id", nullable = false)
    private MovieSchedule movieSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "reservation")
    private List<MovieScheduleSeat> movieScheduleSeats;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    public Reservation(String reservationNum, BigDecimal payment, ReservationType reservationType) {
        boolean isValidateDataFail = reservationNum == null || reservationNum.isBlank() || payment == null || payment.compareTo(BigDecimal.ZERO) <= 0 || reservationType == null;
        if (isValidateDataFail) {
            throw new IllegalArgumentException("Reservation 엔티티 생성 실패");
        }

        this.reservationNum = reservationNum;
        this.payment = payment;
        this.reservationType = reservationType;
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

    public void relatedMember(Member member) {
        if (this.member != null) {
            throw new IllegalStateException("이미 연결된 상태입니다.");
        }

        if (member == null) {
            throw new IllegalArgumentException("연관관계 연결 실패");
        }

        this.member = member;
    }

    public void addMovieScheduleSeat(MovieScheduleSeat movieScheduleSeat) {
        if (movieScheduleSeat == null) {
            throw new IllegalArgumentException("연관관계 추가 실패");
        }

        this.movieScheduleSeats = this.movieScheduleSeats == null ? new ArrayList<>() : this.movieScheduleSeats;

        boolean isDuplicated = this.movieScheduleSeats.stream()
                .anyMatch(ms -> ms.getSeat().getSeatRow().equals(movieScheduleSeat.getSeat().getSeatRow()) && ms.getSeat().getSeatNum() == movieScheduleSeat.getSeat().getSeatNum());
        if (isDuplicated) {
            throw new IllegalStateException("이미 연결된 연관관계입니다.");
        }

        this.movieScheduleSeats.add(movieScheduleSeat);
    }

    public void cancelReservation() {
        if (this.reservationType.equals(ReservationType.CANCEL)) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }

        this.reservationType = ReservationType.CANCEL;
    }

    public void softDelete() {
        if (this.deletedAt != null) {
            throw new IllegalStateException("이미 삭제된 엔티티입니다.");
        }

        this.deletedAt = LocalDateTime.now();
    }
}
