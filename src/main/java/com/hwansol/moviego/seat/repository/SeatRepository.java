package com.hwansol.moviego.seat.repository;

import com.hwansol.moviego.seat.model.Seat;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findBySeatRow(String seatRow);
}
