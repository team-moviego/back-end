package com.hwansol.moviego.genre.controller;

import com.hwansol.moviego.genre.dto.GenreGetDto;
import com.hwansol.moviego.genre.model.Genre;
import com.hwansol.moviego.genre.service.GenreService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<List<GenreGetDto.Response>> getGenreListController() {
        List<Genre> genreList = genreService.getGenreList();

        List<GenreGetDto.Response> response = genreList.stream()
                .map(GenreGetDto.Response::from)
                .toList();

        return ResponseEntity.ok(response);
    }
}
