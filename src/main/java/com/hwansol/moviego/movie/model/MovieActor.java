package com.hwansol.moviego.movie.model;

import com.hwansol.moviego.actor.model.Actor;
import com.hwansol.moviego.config.BaseTImeEntity;
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
public class MovieActor extends BaseTImeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "actor_id", nullable = false)
    private Actor actor;

    public static MovieActor create(Movie movie, Actor actor) {
        if (movie == null || actor == null) {
            throw new IllegalArgumentException("MovieActor 생성 실패");
        }

        MovieActor movieActor = new MovieActor();
        movieActor.relatedActor(actor);
        movieActor.relatedMovie(movie);

        return movieActor;
    }

    public void relatedMovie(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("연관관계 연결 실패");
        }

        this.movie = movie;
    }

    public void relatedActor(Actor actor) {
        if (actor == null) {
            throw new IllegalArgumentException("연관관계 연결 실패");
        }

        this.actor = actor;
    }
}
