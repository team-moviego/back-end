package com.hwansol.moviego.genre.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import com.hwansol.moviego.common.DuplicatedException;
import com.hwansol.moviego.common.ErrorCode;
import com.hwansol.moviego.common.NotFoundException;
import com.hwansol.moviego.genre.dto.GenreCreateDto;
import com.hwansol.moviego.genre.model.Genre;
import com.hwansol.moviego.genre.repository.GenreRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @InjectMocks
    private GenreService genreService;

    @Mock
    private GenreRepository genreRepository;

    @Test
    @DisplayName("전체 장르 리스트 조회 서비스")
    void getGenreList() {
        Genre genre = Genre.builder()
                .name("genre")
                .build();

        when(genreRepository.findAll()).thenReturn(List.of(genre));

        List<Genre> result = genreService.getGenreList();

        assertThat(result.get(0).getName()).isEqualTo("genre");
    }

    @Test
    @DisplayName("장르 생성 서비스")
    void createGenre() {
        GenreCreateDto.Request request = GenreCreateDto.Request.builder()
                .name("genre")
                .build();
        Genre genre = Genre.builder()
                .name("genre")
                .build();

        when(genreRepository.findByName("genre")).thenReturn(Optional.empty());
        when(genreRepository.save(argThat(g -> g.getName().equals("genre")))).thenReturn(genre);

        Genre result = genreService.createGenre(request);

        assertThat(result.getName()).isEqualTo("genre");
    }

    @Test
    @DisplayName("장르 생성 서비스 실패 - 이미 존재하는 장르")
    void createGenreFail1() {
        GenreCreateDto.Request request = GenreCreateDto.Request.builder()
                .name("genre")
                .build();
        Genre genre = Genre.builder()
                .name("genre")
                .build();

        when(genreRepository.findByName("genre")).thenReturn(Optional.of(genre));

        assertThrows(DuplicatedException.class, () -> genreService.createGenre(request), ErrorCode.DUPLICATED.getMessage());
    }

    @Test
    @DisplayName("장르 하드 삭제 서비스")
    void deleteGenre() {
        Genre genre = Genre.builder()
                .name("genre")
                .build();

        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));

        Genre result = genreService.deleteGenre(1L);

        assertThat(result.getName()).isEqualTo("genre");
    }

    @Test
    @DisplayName("장르 하드 삭제 서비스 실패 - 존재하지 않는 엔티티")
    void deleteGenreFail1() {
        when(genreRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> genreService.deleteGenre(1L), ErrorCode.NOT_EXIST.getMessage());
    }
}