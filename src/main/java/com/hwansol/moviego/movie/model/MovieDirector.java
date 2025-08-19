package com.hwansol.moviego.movie.model;

import com.hwansol.moviego.config.BaseTImeEntity;
import com.hwansol.moviego.director.model.Director;
import jakarta.persistence.CascadeType;
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
public class MovieDirector extends BaseTImeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "director_id", nullable = false)
    private Director director;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    public MovieDirector(Movie movie, Director director, LocalDateTime deletedAt) {
        if (movie == null || director == null) {
            throw new IllegalArgumentException("MovieDirector 엔티티 생성 실패");
        }

        this.movie = movie;
        this.director = director;
        this.deletedAt = deletedAt;
    }

    public void relatedMovie(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("연관관계 연결 실패");
        }

        this.movie = movie;
    }

    public void relatedDirector(Director director) {
        if (director == null) {
            throw new IllegalArgumentException("연관관계 연결 실패");
        }

        this.director = director;
    }
}
