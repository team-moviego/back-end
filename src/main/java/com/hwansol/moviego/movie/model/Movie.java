package com.hwansol.moviego.movie.model;

import com.hwansol.moviego.config.BaseTImeEntity;
import com.hwansol.moviego.image.model.Image;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Movie extends BaseTImeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titleKo; // 한글 제목

    @Column(nullable = false)
    private String titleEn; // 영문 제목

    @Column(nullable = false, length = 5000)
    private String description; // 줄거리

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieDirector> directors; // 감독

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieActor> actors; // 배우

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieGenre> genres; // 장르

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images; // 이미지

    @Column
    private int totalShowTime; // 총 상영 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovieRating rating; // 심의 등급

    @Column
    private LocalDateTime releaseDate; // 개봉 날짜

    @Column
    private double gradeAverage; // 평균 평점

    @Column
    private double reservationRate; // 예매율

    @Column
    private LocalDateTime deletedAt; // 삭제 날짜

    @Builder
    public Movie(String titleKo, String titleEn, String description, List<MovieDirector> directors, List<MovieActor> actors, List<MovieGenre> genres, List<Image> images, int totalShowTime, MovieRating rating, LocalDateTime releaseDate, double gradeAverage, double reservationRate, LocalDateTime deletedAt) {
        boolean isValidDataFalse = titleKo == null || titleKo.isBlank() || titleEn == null || titleEn.isBlank() || description == null || description.isBlank() || totalShowTime <= 0 || rating == null || gradeAverage < 0 || reservationRate < 0;

        if (isValidDataFalse) {
            throw new IllegalArgumentException("영화 엔티티 생성 실패");
        }

        this.titleKo = titleKo;
        this.titleEn = titleEn;
        this.description = description;
        this.directors = directors;
        this.actors = actors;
        this.genres = genres;
        this.images = images;
        this.totalShowTime = totalShowTime;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.gradeAverage = gradeAverage;
        this.reservationRate = reservationRate;
        this.deletedAt = deletedAt;
    }

    public void addMovieGenre(MovieGenre movieGenre) {
        if (movieGenre == null) {
            throw new IllegalArgumentException("연관관계 추가 실패");
        }

        this.genres = this.genres == null ? new ArrayList<>() : this.genres;

        if (this.genres.stream().anyMatch(g -> movieGenre.getId() != null && g.getId().equals(movieGenre.getId()))) {
            throw new IllegalArgumentException("이미 연결된 연관관계입니다.");
        }

        this.genres.add(movieGenre);
        movieGenre.relatedMovie(this);
    }

    public void addMovieActor(MovieActor movieActor) {
        if (movieActor == null) {
            throw new IllegalArgumentException("연관관계 추가 실패");
        }

        this.actors = this.actors == null ? new ArrayList<>() : this.actors;

        if (this.actors.stream().anyMatch(a -> movieActor.getId() != null && a.getId().equals(movieActor.getId()))) {
            throw new IllegalArgumentException("이미 연결된 연관관계입니다.");
        }

        this.actors.add(movieActor);
        movieActor.relatedMovie(this);
    }

    public void addMovieDirector(MovieDirector movieDirector) {
        if (movieDirector == null) {
            throw new IllegalArgumentException("연관관계 추가 실패");
        }

        this.directors = this.directors == null ? new ArrayList<>() : this.directors;

        if (this.directors.stream().anyMatch(d -> movieDirector.getId() != null && d.getId().equals(movieDirector.getId()))) {
            throw new IllegalArgumentException("이미 연결된 연관관계입니다.");
        }

        this.directors.add(movieDirector);
        movieDirector.relatedMovie(this);
    }

    public void addImage(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("연관관계 추가 실패");
        }

        this.images = this.images == null ? new ArrayList<>() : this.images;

        if (this.images.stream().anyMatch(f -> image.getId() != null && f.getId().equals(image.getId()))) {
            throw new IllegalArgumentException("이미 연결된 연관관계입니다.");
        }

        this.images.add(image);
        image.relatedMovie(this);
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
