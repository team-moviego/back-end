package com.hwansol.moviego.screen.repository;

import com.hwansol.moviego.screen.model.Screen;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {

    Optional<Screen> findByName(String name);
}
