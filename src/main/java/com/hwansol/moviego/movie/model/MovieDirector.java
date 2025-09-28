package com.hwansol.moviego.movie.model;

import com.hwansol.moviego.config.BaseTImeEntity;
import com.hwansol.moviego.director.model.Director;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieDirector extends BaseTImeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "director_id", nullable = false)
    private Director director;

    public static MovieDirector create(Movie movie, Director director) {
        if (movie == null || director == null) {
            throw new IllegalArgumentException("MovieDirector 생성 실패");
        }

        MovieDirector movieDirector = new MovieDirector();
        movieDirector.relatedDirector(director);
        movieDirector.relatedMovie(movie);

        return movieDirector;
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
