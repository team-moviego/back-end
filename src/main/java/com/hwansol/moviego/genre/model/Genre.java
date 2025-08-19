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
import java.time.LocalDateTime;
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
public class Genre extends BaseTImeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "genre", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MovieGenre> movieGenres;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    public Genre(String name, List<MovieGenre> movieGenres, LocalDateTime deletedAt) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Genre 엔티티 생성 실패");
        }

        this.name = name;
        this.movieGenres = movieGenres;
        this.deletedAt = deletedAt;
    }

    public void withId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Genre 엔티티 생성 실패");
        }

        this.id = id;
    }
}
