package com.springboot.match.stats.repositories;

import com.springboot.match.stats.models.PlayerMatchStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerMatchStatsRepository extends JpaRepository<PlayerMatchStats, Long> {
    boolean existsByPlayerIdAndMatchId(Long playerId, Long matchId);
}
