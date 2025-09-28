package com.hwansol.moviego.seat.service;

import com.hwansol.moviego.common.dto.CommonDto;
import com.hwansol.moviego.common.exception.DeleteException;
import com.hwansol.moviego.common.exception.DuplicatedException;
import com.hwansol.moviego.common.exception.ErrorCode;
import com.hwansol.moviego.common.exception.NotFoundException;
import com.hwansol.moviego.screen.model.Screen;
import com.hwansol.moviego.screen.repository.ScreenRepository;
import com.hwansol.moviego.seat.dto.SeatCreateDto;
import com.hwansol.moviego.seat.model.Seat;
import com.hwansol.moviego.seat.repository.SeatRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final ScreenRepository screenRepository;

    /**
     * 좌석 상세 조회 서비스
     *
     * @param seatId 조회할 좌석 pk
     * @return 조회된 좌석 response dto
     */
    @Transactional(readOnly = true)
    public Seat getSeat(Long seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(NotFoundException::new);
    }

    /**
     * 좌석 전체 리스트 조회 서비스
     *
     * @return 조회된 좌석 전체 리스트
     */
    @Transactional(readOnly = true)
    public List<Seat> getSeatList() {
        List<Seat> seatList = seatRepository.findAll();

        return seatList.stream()
                .sorted(Comparator.comparing((Seat s) -> s.getScreen().getName())
                        .thenComparing(Seat::getSeatRow)
                        .thenComparing(Seat::getSeatNum))
                .toList();
    }

    /**
     * 좌석 추가 생성 서비스
     *
     * @param request SeatCreateDto.Request
     * @return 추가 생성된 좌석 엔티티
     */
    @Transactional
    public Seat createSeat(SeatCreateDto.Request request) {
        Screen screen = screenRepository.findById(request.getScreenId())
                .orElseThrow(NotFoundException::new);

        Seat seat = seatRepository.findBySeatRow(request.getSeatRow().toUpperCase())
                .orElse(null);

        boolean isDuplicated = seat != null && seat.getSeatNum() == request.getSeatNum() && seat.getScreen()
                .getId().equals(screen.getId());
        if (isDuplicated) {
            throw new DuplicatedException();
        }

        Seat newSeat = request.toEntity();
        newSeat.relatedScreen(screen);

        return seatRepository.save(newSeat);
    }

    /**
     * 좌석 삭제 서비스 (하드딜리트)
     *
     * @param seatId  삭제할 좌석 pk
     * @param request SeatDeleteDto.Request
     * @return 삭제된 좌석 엔티티
     */
    @Transactional
    public Seat deleteSeat(Long seatId, CommonDto.DeleteRequest request) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(NotFoundException::new);

        if (!request.getDeleteString().equals(seat.getSeatRow() + seat.getSeatNum())) {
            throw new DeleteException(ErrorCode.HARD_DELETE_FAIL);
        }

        seatRepository.delete(seat);

        return seat;
    }
}
