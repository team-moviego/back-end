package com.hwansol.moviego.reservation.controller;

import com.hwansol.moviego.common.dto.PageListResponseDto;
import com.hwansol.moviego.member.model.PrincipalDetails;
import com.hwansol.moviego.reservation.dto.ReservationGetDto;
import com.hwansol.moviego.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
}
