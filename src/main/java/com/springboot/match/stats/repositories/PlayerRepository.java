package com.springboot.match.stats.repositories;

import com.springboot.match.stats.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    boolean existsByNickname(String nickname);
}
