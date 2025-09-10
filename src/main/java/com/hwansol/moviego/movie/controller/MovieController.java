package com.hwansol.moviego.movie.controller;

import com.hwansol.moviego.movie.dto.MovieGetDto;
import com.hwansol.moviego.movie.service.MovieService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Validated
public class MovieController {

    private final MovieService movieService;

    /**
     * 영화 상세 조회 컨트롤러
     *
     * @param movieId 조회할 영화 엔티티 pk
     * @return 성공 시 200 코드와 조회된 영화 엔티티의 상세 정보를 담고 있는 response dto, 실패 시 에러코드와 에러메시지
     */
    @GetMapping("/movie")
    public ResponseEntity<MovieGetDto.Response> getMovieController(
            @Positive(message = "pk는 0 또는 음수일 수 없습니다.") @RequestParam Long movieId) {
        MovieGetDto.Response response = movieService.getMovie(movieId);

        return ResponseEntity.ok(response);
    }
}
