package com.hwansol.moviego.movieschedule.repository;

import com.hwansol.moviego.movieschedule.model.MovieSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieScheduleRepository extends JpaRepository<MovieSchedule, Long> {

}
