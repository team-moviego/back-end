package com.hwansol.moviego.seat.controller;

import com.hwansol.moviego.common.CommonDto;
import com.hwansol.moviego.seat.dto.SeatCreateDto;
import com.hwansol.moviego.seat.dto.SeatDeleteDto;
import com.hwansol.moviego.seat.dto.SeatGetDto;
import com.hwansol.moviego.seat.model.Seat;
import com.hwansol.moviego.seat.service.SeatService;
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
    public ResponseEntity<SeatGetDto.Response> getSeatController(
            @Positive(message = "pk는 0 또는 음수일 수 없습니다.") @RequestParam Long seatId) {
        Seat seat = seatService.getSeat(seatId);
        SeatGetDto.Response response = SeatGetDto.Response.from(seat);

        return ResponseEntity.ok(response);
    }

    /**
     * 좌석 전체 리스트 조회 컨트롤러
     * 관리자만 조회 가능
     *
     * @return 성공 시 200 코드와 조회된 전체 좌석 response dto 리스트, 실패 시 에러코드와 에러메시지
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SeatGetDto.Response>> getSeatListController() {
        List<Seat> seatList = seatService.getSeatList();
        List<SeatGetDto.Response> responseList = seatList.stream()
                .map(SeatGetDto.Response::from)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    /**
     * 좌석 추가 생성 컨트롤러
     * 관리자만 생성 가능
     *
     * @param request SeatCreateDto.Request
     * @return 성공 시 201 코드와 생성된 좌석 response dto, 실패 시 에러코드와 에러메시지
     */
    @PostMapping("/seat")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonDto> createSeatController(
            @Valid @RequestBody SeatCreateDto.Request request) {
        Seat seat = seatService.createSeat(request);
        CommonDto response = CommonDto.from(seat.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 좌석 삭제 컨트롤러
     * 관리자만 삭제 가능
     *
     * @param seatId  삭제할 좌석 pk
     * @param request SeatDeleteDto.Request
     * @return 성공 시 200 코드와 삭제된 좌석 response dto, 실패 시 에러코드와 에러메시지
     */
    @DeleteMapping("/seat/{seatId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonDto> deleteSeatController(
            @Positive(message = "pk는 0 또는 음수일 수 없습니다.") @PathVariable Long seatId,
            @Valid @RequestBody SeatDeleteDto.Request request) {
        Seat seat = seatService.deleteSeat(seatId, request);
        CommonDto response = CommonDto.from(seat.getId());

        return ResponseEntity.ok(response);
    }
}
