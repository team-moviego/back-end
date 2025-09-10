package com.hwansol.moviego.movieschedule.repository;

import com.hwansol.moviego.movieschedule.model.MovieSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieScheduleRepository extends JpaRepository<MovieSchedule, Long> {

    @Modifying
    @Query("delete " +
           "from MovieSchedule ms " +
           "where ms.id in :ids")
    void bulkDeleteByIds(List<Long> ids);
}
