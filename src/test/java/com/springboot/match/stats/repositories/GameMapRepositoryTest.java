package com.springboot.match.stats.repositories;

import com.springboot.match.stats.models.GameMap;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class GameMapRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    GameMapRepository repository;

    @Test
    void should_return_true_if_map_exists_by_name() {
        GameMap map = new GameMap(null, "Mirage", true, null);
        entityManager.persist(map);

        boolean exists = repository.existsByName("Mirage");

        assertTrue(exists, "True");
    }

    @Test
    void should_return_false_if_map_does_not_exist_by_name() {
        boolean exists = repository.existsByName("Mirage");

        assertFalse(exists, "False");
    }
}
