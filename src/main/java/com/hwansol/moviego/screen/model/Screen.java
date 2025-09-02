package com.hwansol.moviego.screen.model;

import com.hwansol.moviego.config.BaseTImeEntity;
import com.hwansol.moviego.movieschedule.model.MovieSchedule;
import com.hwansol.moviego.seat.model.Seat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
public class Screen extends BaseTImeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieSchedule> movieSchedules;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    public Screen(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Screen 엔티티 생성 실패");
        }

        this.name = name;
    }

    public void addSeat(Seat seat) {
        if (seat == null) {
            throw new IllegalArgumentException("연관관계 추가 실패");
        }

        this.seats = this.seats == null ? new ArrayList<>() : this.seats;

        if (seat.getScreen() != null && seat.getScreen().getName().equals(this.name)) {
            throw new IllegalArgumentException("이미 추가된 연관관계입니다.");
        }

        this.seats.add(seat);

        seat.relatedScreen(this);
    }

    public void addMovieSchedule(MovieSchedule movieSchedule) {
        if (movieSchedule == null) {
            throw new IllegalArgumentException("연관관계 추가 실패");
        }

        this.movieSchedules = this.movieSchedules == null ? new ArrayList<>() : this.movieSchedules;

        if (movieSchedule.getScreen() != null && movieSchedule.getScreen().getName().equals(this.name)) {
            throw new IllegalArgumentException("이미 추가된 연관관계입니다.");
        }

        this.movieSchedules.add(movieSchedule);

        movieSchedule.relatedScreen(this);
    }

    public void softDelete() {
        if (this.deletedAt != null) {
            throw new IllegalStateException("이미 삭제된 데이터입니다.");
        }

        this.deletedAt = LocalDateTime.now();
    }
}
