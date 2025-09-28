package com.hwansol.moviego.director.model;

import com.hwansol.moviego.config.BaseTImeEntity;
import com.hwansol.moviego.movie.model.MovieDirector;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Director extends BaseTImeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "director", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MovieDirector> movieDirectors;

    @Builder
    public Director(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Director 엔티티 생성 실패");
        }

        this.name = name;
    }

    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("감독명 변경 실패");
        }

        this.name = name;
    }
}
