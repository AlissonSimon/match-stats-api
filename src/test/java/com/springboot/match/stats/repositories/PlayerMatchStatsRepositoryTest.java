package com.springboot.match.stats.repositories;

import com.springboot.match.stats.models.GameMap;
import com.springboot.match.stats.models.Match;
import com.springboot.match.stats.models.Player;
import com.springboot.match.stats.models.PlayerMatchStats;
import com.springboot.match.stats.models.enums.ResultType;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ActiveProfiles("test")
class PlayerMatchStatsRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired PlayerMatchStatsRepository repository;

    @Test
    void should_return_true_if_player_id_and_match_id_exist() {
        Player player = new Player(null, "Zigueira", 1000, null, null);
        entityManager.persist(player);

        GameMap map = new GameMap(null, "Luxville", true, null, null);
        entityManager.persist(map);

        Match match = new Match();
        match.setMap(map);
        match = entityManager.persistAndFlush(match);

        PlayerMatchStats stats = new PlayerMatchStats();
        stats.setPlayer(player);
        stats.setMatch(match);
        stats.setResultType(ResultType.VICTORY);
        entityManager.persistAndFlush(stats);

        boolean exists = repository.existsByPlayerIdAndMatchId(player.getId(), match.getId());

        assertTrue(exists, "True");
    }

    @Test
    void should_return_false_if_player_id_and_match_id_dont_exist() {
        boolean exists = repository.existsByPlayerIdAndMatchId(1L, 1L);

        assertFalse(exists, "False");
    }
}
