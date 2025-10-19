package com.hwansol.moviego.reservation.controller;

import com.hwansol.moviego.common.dto.CommonDto;
import com.hwansol.moviego.common.dto.PageListResponseDto;
import com.hwansol.moviego.member.model.PrincipalDetails;
import com.hwansol.moviego.reservation.dto.ReservationCreateDto;
import com.hwansol.moviego.reservation.dto.ReservationGetDto;
import com.hwansol.moviego.reservation.model.Reservation;
import com.hwansol.moviego.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Validated
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 예약 상세 조회 컨트롤러
     * 자기 자신의 예약만 조회 가능
     *
     * @param reservationId    조회할 예약 pk
     * @param principalDetails 조회하는 회원의 principalDetails
     * @return 성공 시 200 코드와 조회된 예약 상세, 실패 시 에러코드와 에러메시지
     */
    @GetMapping("/reservation")
    public ResponseEntity<ReservationGetDto.Response> getReservationController(@RequestParam Long reservationId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String memberId = principalDetails.getUsername();
        ReservationGetDto.Response response = reservationService.getReservation(reservationId, memberId);

        return ResponseEntity.ok(response);
    }

    /**
     * 회원의 예약 리스트 조회 컨트롤러
     *
     * @param principalDetails 조회하는 회원의 principalDetails
     * @param pageable         페이지네이션 정보를 담은 pageable
     * @return 성공 시 200 코드와 조회된 예약 리스트, 실패 시 에러코드와 에러메시지
     */
    @GetMapping
    public ResponseEntity<PageListResponseDto<ReservationGetDto.SimpleResponse>> getReservationListController(@AuthenticationPrincipal PrincipalDetails principalDetails, @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String memberId = principalDetails.getUsername();
        Page<ReservationGetDto.SimpleResponse> reservationList = reservationService.getReservationList(memberId, pageable);
        PageListResponseDto<ReservationGetDto.SimpleResponse> response = PageListResponseDto.from(reservationList);

        return ResponseEntity.ok(response);
    }

    /**
     * 예약 생성 컨트롤러
     *
     * @param request          예약 생성에 필요한 정보 - 예약 타입, 가격, 좌석 정보
     * @param movieScheduleId  예약하는 영화 스케줄 pk
     * @param principalDetails 예약하는 회원의 principalDetails
     * @return 성공 시 201 코드와 생성된 예약 pk, 실패 시 에러코드와 에러메시지
     */
    @PostMapping("/reservation/movieSchedule/{movieScheduleId}")
    public ResponseEntity<CommonDto.Response> createReservationController(@Valid @RequestBody ReservationCreateDto.Request request, @PathVariable Long movieScheduleId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String memberId = principalDetails.getUsername();
        Reservation reservation = reservationService.createReservation(request, movieScheduleId, memberId);
        CommonDto.Response response = CommonDto.Response.from(reservation.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 예약 취소 컨트롤러
     * 본인의 예약만 취소 가능
     * 상영 시간이 이미 지난 예약의 경우 취소 불가능
     *
     * @param reservationId    취소할 예약의 pk
     * @param principalDetails 예약 취소를 진행하는 회원의 principalDetails
     * @return 성공 시 200 코드와 취소한 예약의 pk, 실패 시 에러코드와 에러메시지
     */
    @PostMapping("/reservation/{reservationId}")
    public ResponseEntity<CommonDto.Response> cancelReservationController(@PathVariable Long reservationId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String memberId = principalDetails.getUsername();
        Reservation reservation = reservationService.cancelReservation(reservationId, memberId);
        CommonDto.Response response = CommonDto.Response.from(reservation.getId());

        return ResponseEntity.ok(response);
    }
}
