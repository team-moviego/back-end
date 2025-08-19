package com.hwansol.moviego.movieschedule.model;

import com.hwansol.moviego.config.BaseTImeEntity;
import com.hwansol.moviego.movie.model.Movie;
import com.hwansol.moviego.screen.model.Screen;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
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

    @Column
    private LocalDateTime deletedAt;

    @Builder
    public MovieSchedule(LocalDateTime startDateTime, LocalDateTime endDateTime, Movie movie, Screen screen, LocalDateTime deletedAt) {
        LocalDateTime now = LocalDateTime.now();
        boolean isValidateDataFail = startDateTime == null || startDateTime.isBefore(now) || endDateTime == null || endDateTime.isBefore(startDateTime);

        if (isValidateDataFail) {
            throw new IllegalArgumentException("MovieSchedule 엔티티 생성 실패");
        }

        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.movie = movie;
        this.screen = screen;
        this.deletedAt = deletedAt;
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
}
