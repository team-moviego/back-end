package com.hwansol.moviego.seat.controller;

import com.hwansol.moviego.seat.dto.SeatGetDto;
import com.hwansol.moviego.seat.model.Seat;
import com.hwansol.moviego.seat.service.SeatService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
@Validated
public class SeatController {

    private final SeatService seatService;

    /**
     * 좌석 상세 조회 컨트롤러
     * 관리자만 조회 가능
     *
     * @param seatId 조회할 좌석 pk
     * @return 성공 시 200 코드와 조회된 좌석 response dto, 실패 시 에러코드와 에러메시지
     */
    @GetMapping("/seat")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SeatGetDto.Response> getSeatController(@Positive(message = "pk는 0 또는 음수일 수 없습니다.") @RequestParam Long seatId) {
        Seat seat = seatService.getSeat(seatId);
        SeatGetDto.Response response = SeatGetDto.Response.from(seat);

        return ResponseEntity.ok(response);
    }
}
