package com.hwansol.moviego.genre.controller;

import com.hwansol.moviego.common.CommonDto;
import com.hwansol.moviego.genre.dto.GenreCreateDto;
import com.hwansol.moviego.genre.dto.GenreSimpleGetDto;
import com.hwansol.moviego.genre.model.Genre;
import com.hwansol.moviego.genre.service.GenreService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
@Validated
public class GenreController {

    private final GenreService genreService;

    /**
     * 전체 장르 리스트 조회 컨트롤러
     *
     * @return 성공 시 200 코드와 조회된 장르 리스트, 실패 시 에러코드와 에러메시지
     */
    @GetMapping
    public ResponseEntity<List<GenreSimpleGetDto.Response>> getGenreListController() {
        List<Genre> genreList = genreService.getGenreList();

        List<GenreSimpleGetDto.Response> response = genreList.stream()
                .map(GenreSimpleGetDto.Response::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 장르 생성 컨트롤러
     *
     * @param request GenreCreateDto.Request
     * @return 성공 시 200 코드와 생성된 장르 pk, 실패 시 에러코드와 에러메시지
     */
    @PostMapping("/genre")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonDto.Response> createGenreController(
            @Valid @RequestBody GenreCreateDto.Request request) {
        Genre genre = genreService.createGenre(request);

        CommonDto.Response response = CommonDto.Response.from(genre.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 장르 하드 삭제 컨트롤러
     *
     * @param genreId 삭제할 장르 pk
     * @param request CommonDto.DeleteRequest
     * @return 성공 시 200 코드와 삭제한 장르 pk, 실패 시 에러코드와 에러메시지
     */
    @DeleteMapping("/genre/{genreId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonDto.Response> deleteGenreController(
            @Positive(message = "pk는 0 또는 음수일 수 없습니다.") @PathVariable Long genreId,
            @Valid @RequestBody CommonDto.DeleteRequest request) {
        Genre genre = genreService.deleteGenre(genreId, request);

        CommonDto.Response response = CommonDto.Response.from(genre.getId());

        return ResponseEntity.ok(response);
    }
}
