package com.springboot.match.stats.repositories;

import com.springboot.match.stats.models.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
