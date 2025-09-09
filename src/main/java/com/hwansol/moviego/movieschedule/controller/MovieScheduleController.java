package com.hwansol.moviego.movieschedule.controller;

import com.hwansol.moviego.common.dto.CommonDto;
import com.hwansol.moviego.movieschedule.dto.MovieScheduleCreateDto;
import com.hwansol.moviego.movieschedule.dto.MovieScheduleGetDto;
import com.hwansol.moviego.movieschedule.dto.MovieScheduleSimpleGetDto;
import com.hwansol.moviego.movieschedule.service.MovieScheduleService;
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
    public ResponseEntity<MovieScheduleGetDto.Response> getMovieScheduleController(
            @Positive(message = "pk는 0 또는 음수일 수 없습니다.") @RequestParam Long movieScheduleId
    ) {
        MovieScheduleGetDto.Response response = movieScheduleService.getMovieSchedule(movieScheduleId);

        return ResponseEntity.ok(response);
    }

    /**
     * 전체 또는 특정 상영관의 영화 스케줄 리스트 조회 컨트롤러
     *
     * @param screenId 상영관 pk, 특정 상영관의 영화 스케줄을 조회하고자 할 경우에만 사용함
     * @return 성공 시 200 코드와 조회된 영화 스케줄 리스트의 간단 정보를 담고 있는 response dto 리스트, 실패 시 에러코드와 에러메시지
     */
    @GetMapping
    public ResponseEntity<List<MovieScheduleSimpleGetDto.Response>> getMovieScheduleListController(
            @Positive(message = "pk는 0 또는 음수일 수 없습니다.") @RequestParam(required = false)
            Long screenId
    ) {
        List<MovieScheduleSimpleGetDto.Response> response = movieScheduleService.getMovieScheduleList(screenId);

        return ResponseEntity.ok(response);
    }

    /**
     * 영화 스케줄 생성 컨트롤러
     *
     * @param request 영화 스케줄 생성을 위한 영화 pk, 상영관 pk, 영화 시작/종료 시간 정보를 담고 있는 request dto
     * @return 성공 시 201 코드와 생성된 엔티티의 pk 정보를 담고 있는 response dto, 실패 시 에러코드와 에러메시지
     */
    @PostMapping("/movie-schedule")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonDto.Response> createMovieScheduleController(
            @Valid @RequestBody
            MovieScheduleCreateDto.Request request
    ) {
        CommonDto.Response response = movieScheduleService.createMovieSchedule(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 영화 스케줄 삭제 컨트롤러
     *
     * @param movieScheduleId 삭제할 엔티티 pk
     * @param request         영구 삭제를 위한 문구 정보를 담고 있는 request dto
     * @return 성공 시 200 코드와 삭제된 엔티티 pk 정보를 담고 있는 response dto, 실패 시 에러코드와 에러메시지
     */
    @DeleteMapping("/movie-schedule/{movieScheduleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonDto.Response> deleteMovieScheduleController(
            @Positive(message = "pk는 0 또는 음수일 수 없습니다.") @PathVariable Long movieScheduleId,
            @Valid @RequestBody CommonDto.DeleteRequest request
    ) {
        CommonDto.Response response = movieScheduleService.deleteMovieSchedule(movieScheduleId, request);

        return ResponseEntity.ok(response);
    }
}
