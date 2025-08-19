package com.hwansol.moviego.genre.service;

import com.hwansol.moviego.common.NotFoundException;
import com.hwansol.moviego.genre.model.Genre;
import com.hwansol.moviego.genre.repository.GenreRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {

    private final GenreRepository genreRepository;

    /**
     * 전체 장르 리스트 조회
     *
     * @return 조회된 장르 리스트
     */
    @Transactional(readOnly = true)
    public List<Genre> getGenreList() {
        return genreRepository.findAll();
    }

    /**
     * 장르 하드 삭제
     *
     * @param genreId 삭제할 장르 pk
     * @return 삭제된 장르 엔티티
     */
    @Transactional
    public Genre deleteGenre(Long genreId) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(NotFoundException::new);

        genreRepository.delete(genre);

        return genre;
    }
}
