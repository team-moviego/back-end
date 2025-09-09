package com.hwansol.moviego.movieschedule.service;

import com.hwansol.moviego.common.dto.CommonDto;
import com.hwansol.moviego.common.exception.ErrorCode;
import com.hwansol.moviego.common.exception.NotFoundException;
import com.hwansol.moviego.image.dto.ImageGetDto;
import com.hwansol.moviego.image.model.Image;
import com.hwansol.moviego.image.service.ImageService;
import com.hwansol.moviego.movie.model.Movie;
import com.hwansol.moviego.movie.repository.MovieRepository;
import com.hwansol.moviego.movieschedule.dto.MovieScheduleCreateDto;
import com.hwansol.moviego.movieschedule.dto.MovieScheduleGetDto;
import com.hwansol.moviego.movieschedule.dto.MovieScheduleSimpleGetDto;
import com.hwansol.moviego.movieschedule.exception.CreateMovieScheduleException;
import com.hwansol.moviego.movieschedule.model.MovieSchedule;
import com.hwansol.moviego.movieschedule.model.MovieScheduleSeat;
import com.hwansol.moviego.movieschedule.model.SeatStatus;
import com.hwansol.moviego.movieschedule.repository.MovieScheduleRepository;
import com.hwansol.moviego.screen.model.Screen;
import com.hwansol.moviego.screen.repository.ScreenRepository;
import com.hwansol.moviego.seat.model.Seat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieScheduleService {

    private final MovieScheduleRepository movieScheduleRepository;
    private final ScreenRepository screenRepository;
    private final MovieRepository movieRepository;
    private final ImageService imageService;

    /**
     * 영화 스케줄 상세 조회 서비스
     *
     * @param movieScheduleId 조회할 영화 스케줄 pk
     * @return 조회된 영화 스케줄의 상세 정보를 담고 있는 response dto
     */
    @Transactional(readOnly = true)
    public MovieScheduleGetDto.Response getMovieSchedule(Long movieScheduleId) {
        MovieSchedule movieSchedule = movieScheduleRepository.findById(movieScheduleId)
                .orElseThrow(NotFoundException::new);

        List<Image> movieImages = movieSchedule.getMovie().getImages();
        List<ImageGetDto.Response> movieImageList = new ArrayList<>();

        if (movieImages != null && !movieImages.isEmpty()) {
            movieImageList = imageService.getImageList(movieImages);
        }

        return MovieScheduleGetDto.Response.from(movieSchedule, movieImageList);
    }

    /**
     * 전체 영화 스케줄 리스트 조회 서비스
     *
     * @return 조회된 전체 영화 스케줄의 간단 정보를 담고 있는 response dto 리스트, 없을 경우 empty list
     */
    @Transactional(readOnly = true)
    public List<MovieScheduleSimpleGetDto.Response> getMovieScheduleList() {
        List<MovieSchedule> movieScheduleList = movieScheduleRepository.findAll();
        List<MovieScheduleSimpleGetDto.Response> movieScheduleListResult = new ArrayList<>();

        if (!movieScheduleList.isEmpty()) {
            movieScheduleListResult = movieScheduleList.stream()
                    .map(MovieScheduleSimpleGetDto.Response::from)
                    .sorted(Comparator.comparing(
                                    MovieScheduleSimpleGetDto.Response::getScreenName)
                            .thenComparing(
                                    MovieScheduleSimpleGetDto.Response::getStartDateTime))
                    .toList();
        }

        return movieScheduleListResult;
    }

    /**
     * 특정 상영관의 영화 스케줄 리스트 조회 서비스
     *
     * @param ScreenId 영화 스케줄을 조회할 상영관 pk
     * @return 특정 상영관에서 조회된 영화 스케줄의 간단 정보를 담고 있는 response dto 리스트, 없을 경우 empty list
     */
    @Transactional(readOnly = true)
    public List<MovieScheduleSimpleGetDto.Response> getMovieScheduleListByScreen(Long ScreenId) {
        Screen screen = screenRepository.findById(ScreenId)
                .orElseThrow(NotFoundException::new);

        List<MovieSchedule> movieSchedules = screen.getMovieSchedules();
        List<MovieScheduleSimpleGetDto.Response> movieScheduleList = new ArrayList<>();

        if (movieSchedules != null && !movieSchedules.isEmpty()) {
            movieScheduleList = movieSchedules.stream()
                    .map(MovieScheduleSimpleGetDto.Response::from)
                    .sorted(Comparator.comparing(
                            MovieScheduleSimpleGetDto.Response::getStartDateTime))
                    .toList();
        }

        return movieScheduleList;
    }

    /**
     * 영화 스케줄 생성 서비스
     *
     * @param request 생성할 영화 스케줄의 영화 pk, 상영관 pk, 영화 시작 / 끝 시간 정보를 담고 있는 request dto
     * @return 생성된 영화 스케줄의 pk 정보를 담고 있는 response dto
     */
    @Transactional
    public CommonDto.Response createMovieSchedule(MovieScheduleCreateDto.Request request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(NotFoundException::new);
        Screen screen = screenRepository.findById(request.getScreenId())
                .orElseThrow(NotFoundException::new);

        LocalDateTime now = LocalDateTime.now();
        boolean isDateTimeValidFail = request.getEndDateTime().isBefore(request.getStartDateTime()) ||
                                      request.getStartDateTime().isEqual(request.getEndDateTime()) ||
                                      request.getStartDateTime().isBefore(now) ||
                                      request.getEndDateTime().isBefore(now);

        List<MovieSchedule> movieSchedules = screen.getMovieSchedules();
        boolean isOverLapTimeOrCleanUpTime = false;

        if (movieSchedules != null && !movieSchedules.isEmpty()) {
            isOverLapTimeOrCleanUpTime = movieSchedules.stream()
                    .anyMatch(m -> {
                        boolean isOverLap = request.getStartDateTime().isBefore(m.getEndDateTime()) &&
                                            request.getEndDateTime().isAfter(m.getStartDateTime());

                        boolean isCleanUpTime = Math.abs(Duration.between(request.getStartDateTime(), m.getEndDateTime()).toMinutes()) <= 30 ||
                                                Math.abs(Duration.between(request.getEndDateTime(), m.getStartDateTime()).toMinutes()) <= 30;

                        return isOverLap || isCleanUpTime;
                    });
        }

        if (isDateTimeValidFail || isOverLapTimeOrCleanUpTime) {
            throw new CreateMovieScheduleException(ErrorCode.FAIL_CREATE_MOVIE_SCHEDULE_BY_TIME);
        }

        MovieSchedule newMovieSchedule = request.toEntity();
        newMovieSchedule.relatedMovie(movie);
        newMovieSchedule.relatedScreen(screen);

        List<Seat> seats = screen.getSeats();
        seats.stream()
                .map(s -> {
                    MovieScheduleSeat movieScheduleSeat = MovieScheduleSeat.builder()
                            .seatStatus(SeatStatus.AVAILABLE)
                            .build();
                    movieScheduleSeat.relatedSeat(s);

                    return movieScheduleSeat;
                })
                .forEach(newMovieSchedule::addMovieScheduleSeat);

        MovieSchedule savedMovieSchedule = movieScheduleRepository.save(newMovieSchedule);

        return CommonDto.Response.from(savedMovieSchedule.getId());
    }
}
