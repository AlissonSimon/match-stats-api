package com.springboot.match.stats.repositories;

import com.springboot.match.stats.models.Player;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ActiveProfiles("test")
class PlayerRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    PlayerRepository repository;

    @Test
    void should_return_true_if_player_exists_by_nickname() {
        Player player = new Player(null, "Zigueira", 1000, null, null);
        entityManager.persist(player);

        boolean exists = repository.existsByNickname("Zigueira");

        assertTrue(exists, "True");
    }

    @Test
    void should_return_false_if_player_does_not_exist_by_nickname() {
        boolean exists = repository.existsByNickname("Zigueira");

        assertFalse(exists, "False");
    }
}
