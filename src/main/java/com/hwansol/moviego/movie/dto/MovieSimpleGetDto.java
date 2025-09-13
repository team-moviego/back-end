package com.hwansol.moviego.movie.dto;

import com.hwansol.moviego.movie.model.Movie;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MovieSimpleGetDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;
        private String titleKo;
        private String titleEn;
        private long totalShowTime;

        public Response(Long id, String titleKo, String titleEn, long totalShowTime) {
            boolean isValidateDataFail = id == null || id <= 0 || titleKo == null || titleKo.isBlank() || titleEn == null || titleEn.isBlank() || totalShowTime <= 0;

            if (isValidateDataFail) {
                throw new IllegalArgumentException("MovieSimpleGetDto.Response 생성 실패");
            }

            this.id = id;
            this.titleKo = titleKo;
            this.titleEn = titleEn;
            this.totalShowTime = totalShowTime;
        }

        public static MovieSimpleGetDto.Response from(Movie movie) {
            return MovieSimpleGetDto.Response.builder()
                    .id(movie.getId())
                    .titleKo(movie.getTitleKo())
                    .titleEn(movie.getTitleEn())
                    .totalShowTime(movie.getTotalShowTime())
                    .build();
        }
    }
}
