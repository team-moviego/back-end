package com.hwansol.moviego.reservation.repository;

import com.hwansol.moviego.reservation.model.Reservation;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r " +
           "from Reservation r " +
           "where r.member.userId = :memberId")
    Page<Reservation> findAllWithMemberId(String memberId, Pageable pageable);

    Optional<Reservation> findByReservationNum(String reservationNum);
}
