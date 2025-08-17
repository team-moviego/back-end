package com.hwansol.moviego.file.model;

import com.hwansol.moviego.config.BaseTImeEntity;
import com.hwansol.moviego.movie.model.Movie;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class Image extends BaseTImeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originImageName; // 기존 파일 이름

    @Column(nullable = false)
    private String storeImageName; // 저장된 파일 이름

    @Column(nullable = false)
    private String extension; // 파일 확장자

    @Column(length = 3000, nullable = false)
    private String url;

    @Column
    private long size; // byte

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PosterType posterType; // 메인 또는 일반 포스터

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column
    private LocalDateTime deletedAt;

    public void relatedMovie(Movie movie) {
        if (this.movie != null) {
            throw new IllegalStateException("이미 연결된 상태입니다.");
        }

        if (movie == null) {
            throw new IllegalArgumentException("Movie 엔티티 생성 실패");
        }

        this.movie = movie;
    }
}
