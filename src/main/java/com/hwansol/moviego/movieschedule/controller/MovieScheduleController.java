package com.hwansol.moviego.movieschedule.controller;

import com.hwansol.moviego.movieschedule.dto.MovieScheduleGetDto;
import com.hwansol.moviego.movieschedule.dto.MovieScheduleSimpleGetDto;
import com.hwansol.moviego.movieschedule.service.MovieScheduleService;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movie-schedules")
@RequiredArgsConstructor
@Validated
public class MovieScheduleController {

    private final MovieScheduleService movieScheduleService;

    /**
     * 영화 스케줄 상세 조회 컨트롤러
     *
     * @param movieScheduleId 조회할 영화 스케줄 pk
     * @return 성공 시 200 코드와 조회된 영화 스케줄의 상세 정보를 담고 있는 response dto, 실패 시 에러코드와 에러메시지
     */
    @GetMapping("/movie-schedule")
    public ResponseEntity<MovieScheduleGetDto.Response> getMovieSchedule(
            @Positive(message = "pk는 0 또는 음수일 수 없습니다.") @RequestParam Long movieScheduleId
    ) {
        MovieScheduleGetDto.Response response = movieScheduleService.getMovieSchedule(movieScheduleId);

        return ResponseEntity.ok(response);
    }

    /**
     * 전체 영화 스케줄 리스트 조회 컨트롤러
     *
     * @return 성공 시 200 코드와 조회된 영화 스케줄 리스트의 간단 정보를 담고 있는 response dto 리스트, 실패 시 에러코드와 에러메시지
     */
    @GetMapping
    public ResponseEntity<List<MovieScheduleSimpleGetDto.Response>> getMovieScheduleList() {
        List<MovieScheduleSimpleGetDto.Response> response = movieScheduleService.getMovieScheduleList();

        return ResponseEntity.ok(response);
    }
}
