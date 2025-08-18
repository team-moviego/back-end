package com.hwansol.moviego.movie.dto;

import com.hwansol.moviego.image.dto.ImageGetDto;
import com.hwansol.moviego.movie.model.Movie;
import com.hwansol.moviego.movie.model.MovieRating;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MovieGetDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;
        private String titleKo;
        private String titleEn;
        private String description;
        private List<String> directors;
        private List<String> actors;
        private List<String> genres;
        private List<ImageGetDto.Response> images;
        private int totalShowTime;
        private MovieRating rating;
        private LocalDateTime releaseDate;
        private double gradeAverage;
        private double reservationRate;

        public Response(Long id, String titleKo, String titleEn, String description, List<String> directors, List<String> actors, List<String> genres, List<ImageGetDto.Response> images, int totalShowTime, MovieRating rating, LocalDateTime releaseDate, double gradeAverage, double reservationRate) {
            boolean isValidateDataFail = id == null || id <= 0 || titleKo == null || titleKo.isBlank() || titleEn == null || titleEn.isBlank() || description == null || description.isBlank() || directors == null || directors.isEmpty() || actors == null || actors.isEmpty() || genres == null || genres.isEmpty() || totalShowTime <= 0 || rating == null || releaseDate == null || gradeAverage < 0 || reservationRate < 0;

            if (isValidateDataFail) {
                throw new IllegalArgumentException("MovieGetDto.Response 생성 실패");
            }

            this.id = id;
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
        }

        public static Response from(Movie movie, List<ImageGetDto.Response> imageList) {
            List<String> actorNames = movie.getActors().stream()
                    .map(ma -> ma.getActor().getName())
                    .toList();
            List<String> directorNames = movie.getDirectors().stream()
                    .map(md -> md.getDirector().getName())
                    .toList();
            List<String> genreNames = movie.getGenres().stream()
                    .map(mg -> mg.getGenre().getName())
                    .toList();

            return Response.builder()
                    .actors(actorNames)
                    .genres(genreNames)
                    .images(imageList)
                    .rating(movie.getRating())
                    .id(movie.getId())
                    .description(movie.getDescription())
                    .directors(directorNames)
                    .gradeAverage(movie.getGradeAverage())
                    .releaseDate(movie.getReleaseDate())
                    .reservationRate(movie.getReservationRate())
                    .totalShowTime(movie.getTotalShowTime())
                    .titleEn(movie.getTitleEn())
                    .titleKo(movie.getTitleKo())
                    .build();
        }
    }
}
