package com.hwansol.moviego.movie.service;

import com.hwansol.moviego.common.exception.AlreadyDeletedException;
import com.hwansol.moviego.common.exception.NotFoundException;
import com.hwansol.moviego.image.dto.ImageGetDto;
import com.hwansol.moviego.image.service.ImageService;
import com.hwansol.moviego.movie.dto.MovieGetDto;
import com.hwansol.moviego.movie.model.Movie;
import com.hwansol.moviego.movie.model.OrderType;
import com.hwansol.moviego.movie.repository.MovieRepository;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final ImageService imageService;

    /**
     * 영화 단건 조회 서비스
     *
     * @param id - 영화 pk
     * @return 조회된 영화
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "movie", key = "#id")
    public MovieGetDto.Response getMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        List<ImageGetDto.Response> imageList = imageService.getImageList(movie.getImages());

        return MovieGetDto.Response.from(movie, imageList);
    }

    /**
     * 영화 전체 리스트 조회 서비스
     *
     * @param word            검색어
     * @param genreName       장르
     * @param orderTypeString 정렬 방식
     * @param pageable        페이지네이션 조건
     * @return 페이지네이션 처리된 영화 전체 리스트
     */
    @Transactional(readOnly = true)
    public Page<Movie> getMovieList(String word, String genreName, String orderTypeString,
            Pageable pageable) {
        OrderType orderType = Arrays.stream(OrderType.values())
                .filter(o -> o.name().equals(orderTypeString.toUpperCase()))
                .findAny()
                .orElse(null);

        if (orderType == null) {
            throw new NotFoundException();
        }

        return movieRepository.getMovieListWithOptions(word, genreName, orderType, pageable);
    }

    /**
     * 영화 삭제 서비스
     *
     * @param id - 삭제할 영화 pk
     */
    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        if (movie.getDeletedAt() != null) {
            throw new AlreadyDeletedException();
        }

        movie.softDelete();
    }
}
