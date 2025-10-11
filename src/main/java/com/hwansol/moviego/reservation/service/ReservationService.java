package com.hwansol.moviego.reservation.service;

import com.hwansol.moviego.common.exception.AuthException;
import com.hwansol.moviego.common.exception.ErrorCode;
import com.hwansol.moviego.common.exception.NotFoundException;
import com.hwansol.moviego.member.model.Member;
import com.hwansol.moviego.member.repository.MemberRepository;
import com.hwansol.moviego.movieschedule.model.MovieSchedule;
import com.hwansol.moviego.movieschedule.model.MovieScheduleSeat;
import com.hwansol.moviego.movieschedule.model.SeatStatus;
import com.hwansol.moviego.movieschedule.repository.MovieScheduleRepository;
import com.hwansol.moviego.movieschedule.repository.MovieScheduleSeatRepository;
import com.hwansol.moviego.reservation.dto.ReservationCreateDto;
import com.hwansol.moviego.reservation.dto.ReservationGetDto;
import com.hwansol.moviego.reservation.exception.ReserveSeatException;
import com.hwansol.moviego.reservation.model.Reservation;
import com.hwansol.moviego.reservation.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private static int RESERVATION_SEQUENCE = 0;
    private final ReservationRepository reservationRepository;
    private final MovieScheduleRepository movieScheduleRepository;
    private final MovieScheduleSeatRepository movieScheduleSeatRepository;
    private final MemberRepository memberRepository;

    /**
     * 예약 상세 조회 서비스
     *
     * @param reservationId 조회할 예약 pk
     * @return 조회된 예약
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

    /**
     * 회원의 예약 리스트 조회 서비스
     *
     * @param memberId 회원 아이디
     * @param pageable 페이징 조건
     * @return 회원의 예약 리스트
     */
    @Transactional(readOnly = true)
    public Page<ReservationGetDto.SimpleResponse> getReservationList(String memberId, Pageable pageable) {
        Page<Reservation> reservationList = reservationRepository.findAllWithMemberId(memberId, pageable);

        return reservationList.map(ReservationGetDto.SimpleResponse::from);
    }

    /**
     * 예약 생성 서비스
     *
     * @param request         예약 생성 request
     * @param movieScheduleId 영화 스케줄 pk
     * @param memberId        회원 pk
     * @return 생성된 예약 엔티티
     */
    @Transactional
    public Reservation createReservation(ReservationCreateDto.Request request, Long movieScheduleId, String memberId) {
        List<MovieScheduleSeat> movieScheduleSeatList = request.getMovieScheduleSeatIds().stream()
                .map(l -> {
                    MovieScheduleSeat movieScheduleSeat = movieScheduleSeatRepository.findById(l)
                            .orElseThrow(NotFoundException::new);

                    if (movieScheduleSeat.getSeatStatus().equals(SeatStatus.UNAVAILABLE)) {
                        throw new ReserveSeatException(ErrorCode.ALREADY_RESERVED_SEAT);
                    }

                    return movieScheduleSeat;
                })
                .toList();

        String reservationNum = createReservationNum();
        Reservation reservation = request.toEntity(reservationNum);

        MovieSchedule movieSchedule = movieScheduleRepository.findById(movieScheduleId)
                .orElseThrow(NotFoundException::new);
        Member member = memberRepository.findByUserId(memberId)
                .orElseThrow(NotFoundException::new);

        reservation.relatedMember(member);
        reservation.relatedMovieSchedule(movieSchedule);
        movieScheduleSeatList.forEach(ms -> {
            ms.reserveSeat();
            reservation.addMovieScheduleSeat(ms);
        });

        return reservationRepository.save(reservation);
    }

    // 예약 번호 생성 메서드
    // 예: 날짜 + 시간 + 2자리 시퀀스
    // 2025101085494000
    private String createReservationNum() {
        if (RESERVATION_SEQUENCE >= 100) {
            RESERVATION_SEQUENCE = 0;
        }

        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonth().getValue();
        int dayOfMonth = now.getDayOfMonth();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();

        return String.format("%d%02d%02d%02d%02d%02d%06d", year, month, dayOfMonth, hour, minute, second, RESERVATION_SEQUENCE++);
    }
}
