package com.springboot.match.stats.services;

import com.springboot.match.stats.dtos.PlayerRequestDTO;
import com.springboot.match.stats.dtos.PlayerResponseDTO;
import com.springboot.match.stats.models.Player;
import com.springboot.match.stats.repositories.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlayerServiceTest {
    @InjectMocks
    private PlayerService service;
    @Mock
    private PlayerRepository repository;
    private Player playerEntity;
    private PlayerRequestDTO playerRequest;

    @BeforeEach
    void setUp() {
        playerEntity = new Player();
        playerEntity.setId(1L);
        playerEntity.setNickname("Fallen");
        playerEntity.setElo(1000);

        playerRequest = new PlayerRequestDTO("Fallen");
    }

    @Test
    void should_return_all_players() {
        List<Player> list = List.of(playerEntity);
        when(repository.findAll()).thenReturn(list);

        List<PlayerResponseDTO> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(playerEntity.getId(), result.get(0).id());
        assertEquals(playerEntity.getNickname(), result.get(0).nickname());
        assertEquals(playerEntity.getElo(), result.get(0).elo());

        verify(repository, times(1)).findAll();
    }

    @Test
    void should_find_player_by_id_when_id_exists() {
        when(repository.findById(1L)).thenReturn(Optional.of(playerEntity));

        PlayerResponseDTO result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Fallen", result.nickname());
        assertEquals(1000, result.elo());

        verify(repository, times(1)).findById(1L);
    }

    @Test
    void should_insert_an_player_and_return_player_response_dto() {
        when(repository.save(any(Player.class))).thenReturn(playerEntity);

        PlayerResponseDTO result = service.insert(playerRequest);

        assertNotNull(result);
        assertEquals(playerEntity.getId(), result.id());
        assertEquals(playerEntity.getNickname(), result.nickname());
        assertEquals(playerEntity.getElo(), result.elo());

        verify(repository, times(1)).save(any(Player.class));
    }

    @Test
    void should_update_an_player_and_return_updated_player() {
        when(repository.getReferenceById(1L)).thenReturn(playerEntity);
        when(repository.save(any(Player.class))).thenReturn(playerEntity);

        PlayerRequestDTO updatedRequest = new PlayerRequestDTO("Coldzera");
        PlayerResponseDTO updatedResponse = service.update(1L, updatedRequest);

        assertNotNull(updatedResponse);
        assertEquals("Coldzera", updatedResponse.nickname());

        verify(repository, times(1)).getReferenceById(1L);
        verify(repository, times(1)).save(playerEntity);
    }

    @Test
    void should_delete_an_player_by_id() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void should_delete_all_players() {
        service.deleteAll();

        verify(repository, times(1)).deleteAll();
    }

    @Test
    void should_throw_exception_when_id_is_not_found() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findById(2L));

        verify(repository, times(1)).findById(2L);
    }

    @Test
    void should_throw_exception_when_id_is_not_deleted() {
        when(repository.existsById(2L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.delete(2L));

        verify(repository, never()).deleteById(2L);
    }
}
