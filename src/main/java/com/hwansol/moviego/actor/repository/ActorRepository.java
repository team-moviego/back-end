package com.hwansol.moviego.actor.repository;

import com.hwansol.moviego.actor.model.Actor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {

    Optional<Actor> findByName(String name);
}
