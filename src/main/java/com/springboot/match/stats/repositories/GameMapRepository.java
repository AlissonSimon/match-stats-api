package com.springboot.match.stats.repositories;

import com.springboot.match.stats.models.GameMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameMapRepository extends JpaRepository<GameMap, Long> {
    boolean existsByName(String name);
}
