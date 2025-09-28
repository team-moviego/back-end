package com.hwansol.moviego.movie.controller;

import com.hwansol.moviego.common.dto.CommonDto;
import com.hwansol.moviego.common.dto.PageListResponseDto;
import com.hwansol.moviego.movie.dto.MovieCreateDto;
import com.hwansol.moviego.movie.dto.MovieGetDto;
import com.hwansol.moviego.movie.dto.MovieSimpleGetDto;
import com.hwansol.moviego.movie.model.Movie;
import com.hwansol.moviego.movie.service.MovieService;
import com.hwansol.moviego.validation.IsImage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 영화 전체 리스트 조회 컨트롤러
     *
     * @param word            검색어
     * @param genreName       장르
     * @param orderTypeString 정렬 방식
     * @param pageable        페이지네이션 조건
     * @return 성공 시 200 코드와 페이지네이션 처리되어 조회된 전체 영화 리스트의 간단 정보를 담고 있는 response dto 리스트, 실패 시 에러코드와 에러메시지
     */
    @GetMapping
    public ResponseEntity<PageListResponseDto<MovieSimpleGetDto.Response>> getMovieListController(
            @NotEmpty(message = "검색어는 빈 문자열일 수 없습니다.") @RequestParam(required = false) String word,
            @NotEmpty(message = "장르 이름은 빈 문자열일 수 없습니다.") @RequestParam(required = false)
            String genreName,
            @NotEmpty(message = "정렬 enum값은 빈 문자열일 수 없습니다.") @RequestParam(required = false)
            String orderTypeString, @PageableDefault Pageable pageable) {
        Page<Movie> movieList = movieService.getMovieList(word, genreName, orderTypeString, pageable);
        Page<MovieSimpleGetDto.Response> responseList = movieList
                .map(MovieSimpleGetDto.Response::from);

        PageListResponseDto<MovieSimpleGetDto.Response> response = PageListResponseDto.from(responseList);

        return ResponseEntity.ok(response);
    }

    /**
     * 영화 생성 컨트롤러
     * 관리자만 생성 가능
     *
     * @param imageList 포스터 이미지 리스트, 첫 번째 인덱스 포스터가 메인 포스터
     * @param request   영화 생성을 위한 정보를 담고 있는 request dto
     * @return 성공 시 201 코드와 생성된 영화 엔티티의 pk 정보를 담고 있는 response dto, 실패 시 에러코드와 에러메시지
     */
    @PostMapping("/movie")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonDto.Response> createMovieController(
            @RequestPart List<@IsImage MultipartFile> imageList,
            @Valid @RequestPart MovieCreateDto.Request request) {
        CommonDto.Response response = movieService.createMovie(request, imageList);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 영화 소프트 삭제 컨트롤러
     * 관리자만 삭제 가능
     *
     * @param movieId 삭제할 영화 엔티티의 pk
     * @return 성공 시 200 코드와 삭제된 영화 엔티티의 pk 정보를 담고 있는 response dto, 실패 시 에러코드와 에러메시지
     */
    @DeleteMapping("/movie/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonDto.Response> deleteMovieController(
            @Positive(message = "pk는 0 또는 음수일 수 없습니다.") @PathVariable Long movieId) {
        CommonDto.Response response = movieService.deleteMovie(movieId);

        return ResponseEntity.ok(response);
    }
}
