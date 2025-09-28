package com.hwansol.moviego.movie.repository;

import com.hwansol.moviego.movie.model.Movie;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, MovieCustomRepository {

    Optional<Movie> findByTitleKo(String titleKo);
}
