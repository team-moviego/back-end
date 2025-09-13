package com.hwansol.moviego.movie.dto;

import com.hwansol.moviego.movie.model.Movie;
import com.hwansol.moviego.movie.model.MovieRating;
import com.hwansol.moviego.validation.IsEnum;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MovieCreateDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotBlank(message = "한국어 제목을 입력해주세요.")
        private String titleKo;

        @NotBlank(message = "영어 제목을 입력해주세요.")
        private String titleEn;

        @NotBlank(message = "줄거리를 입력해주세요.")
        @Size(max = 5000, message = "줄거리는 5000자까지 입력 가능합니다.")
        private String description;

        @NotNull(message = "감독 이름을 입력해주세요.")
        private List<@NotBlank(message = "감독 이름을 입력해주세요.") String> directorNames;

        @NotNull(message = "배우 이름을 입력해주세요.")
        private List<@NotBlank(message = "배우 이름을 입력해주세요.") String> actorNames;

        @NotNull(message = "장르를 입력해주세요.")
        private List<@NotBlank(message = "장르를 입력해주세요.") String> genreNames;

        @Positive(message = "총 상영 시간은 0 또는 음수일 수 없습니다.")
        private int totalShowTime;

        @IsEnum(message = "올바른 enum값을 입력해주세요.")
        private MovieRating rating;

        @NotNull(message = "개봉 날짜를 입력해주세요.")
        @FutureOrPresent(message = "개봉 날짜는 현재 또는 미래의 날짜이어야 합니다.")
        private LocalDateTime releaseDateTime;

        public Request(
                String titleKo, String titleEn, String description, List<String> directorNames,
                List<String> actorNames, List<String> genreNames, int totalShowTime,
                MovieRating rating, LocalDateTime releaseDateTime
        ) {
            LocalDateTime now = LocalDateTime.now();

            boolean isValidDataFail = titleKo == null || titleKo.isBlank() || titleEn == null || titleEn.isBlank() || description == null || description.isBlank() || directorNames == null || directorNames.isEmpty() || actorNames == null || actorNames.isEmpty() || genreNames == null || genreNames.isEmpty() || totalShowTime <= 0 || rating == null || releaseDateTime == null || releaseDateTime.isBefore(now);

            if (isValidDataFail) {
                throw new IllegalArgumentException("MovieCreateDto.Request 생성 실패");
            }

            this.titleKo = titleKo;
            this.titleEn = titleEn;
            this.description = description;
            this.directorNames = directorNames;
            this.actorNames = actorNames;
            this.genreNames = genreNames;
            this.totalShowTime = totalShowTime;
            this.rating = rating;
            this.releaseDateTime = releaseDateTime;
        }

        public Movie toEntity() {
            return Movie.builder()
                    .titleKo(this.titleKo)
                    .titleEn(this.titleEn)
                    .description(this.description)
                    .totalShowTime(this.totalShowTime)
                    .rating(this.rating)
                    .releaseDate(this.releaseDateTime)
                    .build();
        }
    }
}
