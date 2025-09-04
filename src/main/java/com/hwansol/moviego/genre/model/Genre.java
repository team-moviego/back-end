package com.hwansol.moviego.genre.model;

import com.hwansol.moviego.config.BaseTImeEntity;
import com.hwansol.moviego.movie.model.MovieGenre;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Genre extends BaseTImeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "genre", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MovieGenre> movieGenres;

    @Builder
    public Genre(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Genre 엔티티 생성 실패");
        }

        this.name = name;
    }

    // 테스트코드용
    public void withId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Genre 엔티티 생성 실패");
        }

        this.id = id;
    }

    public void addMovieGenre(MovieGenre movieGenre) {
        if (movieGenre == null) {
            throw new IllegalArgumentException("연관관계 추가 실패");
        }

        this.movieGenres = this.movieGenres == null ? new ArrayList<>() : this.movieGenres;

        if (!this.movieGenres.isEmpty()) {
            boolean isDuplicated = this.movieGenres.stream()
                    .anyMatch(m -> m.getGenre().getName().equals(movieGenre.getGenre().getName()));

            if (isDuplicated) {
                throw new IllegalArgumentException("중복된 연관관계입니다.");
            }
        }

        this.movieGenres.add(movieGenre);
        movieGenre.relatedGenre(this);
    }
}
