package com.hwansol.moviego.movieschedule.model;

import com.hwansol.moviego.config.BaseTImeEntity;
import com.hwansol.moviego.movie.model.Movie;
import com.hwansol.moviego.screen.model.Screen;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieSchedule extends BaseTImeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    private Screen screen;

    @OneToMany(mappedBy = "movieSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieScheduleSeat> movieScheduleSeats;

    @Builder
    public MovieSchedule(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalDateTime now = LocalDateTime.now();
        boolean isValidateDataFail = startDateTime == null || startDateTime.isBefore(
                now) || endDateTime == null || endDateTime.isBefore(startDateTime);

        if (isValidateDataFail) {
            throw new IllegalArgumentException("MovieSchedule 엔티티 생성 실패");
        }

        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public void relatedMovie(Movie movie) {
        if (this.movie != null) {
            throw new IllegalStateException("이미 연결된 상태입니다.");
        }

        if (movie == null) {
            throw new IllegalArgumentException("연관관계 연결 실패");
        }

        this.movie = movie;
    }

    public void relatedScreen(Screen screen) {
        if (this.screen != null) {
            throw new IllegalStateException("이미 연결된 상태입니다.");
        }

        if (screen == null) {
            throw new IllegalArgumentException("연관관계 연결 실패");
        }

        this.screen = screen;
    }

    public void addMovieScheduleSeat(MovieScheduleSeat movieScheduleSeat) {
        if (movieScheduleSeat == null) {
            throw new IllegalArgumentException("연관관계 추가 실패");
        }

        this.movieScheduleSeats =
                this.movieScheduleSeats == null ? new ArrayList<>() : this.movieScheduleSeats;

        boolean isDuplicated = this.movieScheduleSeats.stream()
                .anyMatch(
                        m -> m.getSeat().getSeatNum() == movieScheduleSeat.getSeat().getSeatNum());

        if (isDuplicated) {
            throw new IllegalArgumentException("이미 연결된 연관관계 입니다.");
        }

        this.movieScheduleSeats.add(movieScheduleSeat);
        movieScheduleSeat.relatedMovieSchedule(this);
    }
}
