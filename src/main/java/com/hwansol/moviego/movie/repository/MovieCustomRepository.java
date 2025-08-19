package com.hwansol.moviego.movie.repository;

import com.hwansol.moviego.movie.model.Movie;
import com.hwansol.moviego.movie.model.OrderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieCustomRepository {

    Page<Movie> getMovieListWithOptions(String word, String genre, OrderType orderType, Pageable pageable);
}
