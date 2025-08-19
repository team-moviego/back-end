package com.hwansol.moviego.director.repository;

import com.hwansol.moviego.director.model.Director;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {

    Optional<Director> findByName(String name);
}
