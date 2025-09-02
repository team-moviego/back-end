package com.hwansol.moviego.seat.service;

import com.hwansol.moviego.common.NotFoundException;
import com.hwansol.moviego.seat.dto.SeatGetDto;
import com.hwansol.moviego.seat.model.Seat;
import com.hwansol.moviego.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    /**
     * 좌석 상세 조회 서비스
     *
     * @param seatId 조회할 좌석 pk
     * @return 조회된 좌석 response dto
     */
    @Transactional(readOnly = true)
    public SeatGetDto.Response getSeat(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(NotFoundException::new);

        return SeatGetDto.Response.from(seat);
    }
}
