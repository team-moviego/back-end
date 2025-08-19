package com.hwansol.moviego.genre.repository;

import com.hwansol.moviego.genre.model.Genre;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    Optional<Genre> findByName(String name);
}
