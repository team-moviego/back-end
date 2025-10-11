package com.hwansol.moviego.movieschedule.repository;

import com.hwansol.moviego.movieschedule.model.MovieScheduleSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieScheduleSeatRepository extends JpaRepository<MovieScheduleSeat, Long> {

}
