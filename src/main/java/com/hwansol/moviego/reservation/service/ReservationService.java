package com.hwansol.moviego.reservation.service;

import com.hwansol.moviego.common.exception.AuthException;
import com.hwansol.moviego.common.exception.ErrorCode;
import com.hwansol.moviego.common.exception.NotFoundException;
import com.hwansol.moviego.reservation.dto.ReservationGetDto;
import com.hwansol.moviego.reservation.model.Reservation;
import com.hwansol.moviego.reservation.repository.ReservationRepository;
import java.time.LocalDate;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;

    /**
     * 예약 상세 조회 서비스
     *
     * @param reservationId 조회할 예약 pk
     * @return 조회된 예약 엔티티
     */
    @Transactional(readOnly = true)
    public ReservationGetDto.Response getReservation(Long reservationId, String memberId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(NotFoundException::new);
        String userId = reservation.getMember().getUserId();

        if (!memberId.equals(userId)) {
            throw new AuthException(ErrorCode.FORBIDDEN);
        }

        return ReservationGetDto.Response.from(reservation);
    }

    // 예약 번호 생성 메서드
    // 예: 날짜 + 6자리 난수
    // 20251010854940
    private String createReservationNum() {
        Random random = new Random();
        int randomNum = random.nextInt(1000000);

        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int value = now.getMonth().getValue();
        int dayOfMonth = now.getDayOfMonth();

        return year + String.format("%2s", value).replace(" ", "0") + String.format("%2s", dayOfMonth).replace(" ", "0") + String.format("%6s", randomNum).replace(" ", "0");
    }
}
