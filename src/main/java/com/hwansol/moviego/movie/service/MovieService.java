package com.hwansol.moviego.movie.service;

import com.hwansol.moviego.actor.model.Actor;
import com.hwansol.moviego.actor.repository.ActorRepository;
import com.hwansol.moviego.common.dto.CommonDto;
import com.hwansol.moviego.common.exception.AlreadyDeletedException;
import com.hwansol.moviego.common.exception.DuplicatedException;
import com.hwansol.moviego.common.exception.NotFoundException;
import com.hwansol.moviego.director.model.Director;
import com.hwansol.moviego.director.repository.DirectorRepository;
import com.hwansol.moviego.genre.model.Genre;
import com.hwansol.moviego.genre.repository.GenreRepository;
import com.hwansol.moviego.image.dto.ImageGetDto;
import com.hwansol.moviego.image.model.Image;
import com.hwansol.moviego.image.service.ImageService;
import com.hwansol.moviego.movie.dto.MovieCreateDto;
import com.hwansol.moviego.movie.dto.MovieGetDto;
import com.hwansol.moviego.movie.model.Movie;
import com.hwansol.moviego.movie.model.MovieActor;
import com.hwansol.moviego.movie.model.MovieDirector;
import com.hwansol.moviego.movie.model.MovieGenre;
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
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;
    private final GenreRepository genreRepository;
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
     * 영화 생성 서비스
     *
     * @param request 영화 생성에 필요한 정보를 담고 있는 request dto
     * @param images  영화 포스터 이미지를 담고 있는 MultipartFile 리스트
     * @return 생성된 영화 엔티티의 pk 정보를 담고 있는 response dto
     */
    @Transactional
    public CommonDto.Response createMovie(MovieCreateDto.Request request, List<MultipartFile> images) {
        Movie existMovie = movieRepository.findByTitleKo(request.getTitleKo())
                .orElse(null);

        if (existMovie != null) {
            throw new DuplicatedException();
        }

        Movie newMovie = request.toEntity();

        request.getActorNames().stream()
                .map(a -> {
                    Actor actor = actorRepository.findByName(a)
                            .orElse(null);

                    if (actor == null) {
                        actor = Actor.builder()
                                .name(a)
                                .build();
                    }

                    return MovieActor.create(newMovie, actor);
                })
                .forEach(newMovie::addMovieActor);

        request.getDirectorNames().stream()
                .map(d -> {
                    Director director = directorRepository.findByName(d)
                            .orElse(null);

                    if (director == null) {
                        director = Director.builder()
                                .name(d)
                                .build();
                    }

                    return MovieDirector.create(newMovie, director);
                })
                .forEach(newMovie::addMovieDirector);

        request.getGenreNames().stream()
                .map(g -> {
                    Genre genre = genreRepository.findByName(g)
                            .orElse(null);

                    if (genre == null) {
                        genre = Genre.builder()
                                .name(g)
                                .build();
                    }

                    return MovieGenre.create(newMovie, genre);
                })
                .forEach(newMovie::addMovieGenre);

        List<Image> movieImages = imageService.createFile(images);
        movieImages.forEach(newMovie::addImage);

        Movie savedMovie = movieRepository.save(newMovie);

        return CommonDto.Response.from(savedMovie.getId());
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
