package com.springboot.match.stats.services;

import com.springboot.match.stats.dtos.player.PlayerRequestDTO;
import com.springboot.match.stats.dtos.player.PlayerResponseDTO;
import com.springboot.match.stats.models.Player;
import com.springboot.match.stats.repositories.PlayerRepository;
import com.springboot.match.stats.services.exceptions.NicknameAlreadyExistsException;
import com.springboot.match.stats.services.exceptions.ResourceNotFoundException;
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
    private static final Long ID_EXISTENT = 1L;
    private static final Long ID_NON_EXISTENT = 99L;
    private static final int DEFAULT_ELO = 1000;
    private static final String NICKNAME_EXISTENT = "Fallen";
    private static final String NICKNAME_UPDATED = "Coldzera";

    @InjectMocks
    private PlayerService service;
    @Mock
    private PlayerRepository repository;
    private Player playerEntity;
    private PlayerRequestDTO playerRequest;

    @BeforeEach
    void setUp() {
        playerEntity = new Player();
        playerEntity.setId(ID_EXISTENT);
        playerEntity.setNickname(NICKNAME_EXISTENT);
        playerEntity.setElo(DEFAULT_ELO);

        playerRequest = new PlayerRequestDTO(NICKNAME_EXISTENT);
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
        when(repository.findById(ID_EXISTENT)).thenReturn(Optional.of(playerEntity));

        PlayerResponseDTO result = service.findById(ID_EXISTENT);

        assertNotNull(result);
        assertEquals(ID_EXISTENT, result.id());
        assertEquals(NICKNAME_EXISTENT, result.nickname());
        assertEquals(DEFAULT_ELO, result.elo());

        verify(repository, times(1)).findById(ID_EXISTENT);
    }

    @Test
    void should_insert_player_and_return_player_response_dto() {
        when(repository.save(any(Player.class))).thenReturn(playerEntity);

        PlayerResponseDTO result = service.insert(playerRequest);

        assertNotNull(result);
        assertEquals(playerEntity.getId(), result.id());
        assertEquals(playerEntity.getNickname(), result.nickname());
        assertEquals(playerEntity.getElo(), result.elo());

        verify(repository, times(1)).save(any(Player.class));
    }

    @Test
    void should_update_player_and_return_updated_player() {
        when(repository.findById(ID_EXISTENT)).thenReturn(Optional.of(playerEntity));
        when(repository.save(any(Player.class))).thenReturn(playerEntity);

        PlayerRequestDTO updatedRequest = new PlayerRequestDTO(NICKNAME_UPDATED);
        PlayerResponseDTO updatedResponse = service.update(ID_EXISTENT, updatedRequest);

        assertNotNull(updatedResponse);
        assertEquals(NICKNAME_UPDATED, updatedResponse.nickname());

        verify(repository, times(1)).findById(ID_EXISTENT);
        verify(repository, times(1)).save(playerEntity);
    }

    @Test
    void should_delete_player_by_id() {
        when(repository.existsById(ID_EXISTENT)).thenReturn(true);

        service.delete(ID_EXISTENT);

        verify(repository, times(1)).deleteById(ID_EXISTENT);
    }

    @Test
    void should_delete_all_players() {
        service.deleteAll();

        verify(repository, times(1)).deleteAll();
    }

    @Test
    void should_throw_exception_when_id_is_not_found() {
        when(repository.findById(ID_NON_EXISTENT)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(ID_NON_EXISTENT));

        verify(repository, times(1)).findById(ID_NON_EXISTENT);
    }

    @Test
    void should_throw_exception_when_id_is_not_deleted() {
        when(repository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(ID_NON_EXISTENT));

        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void should_throw_exception_on_duplicate_nickname_updating() {
        when(repository.findById(ID_EXISTENT)).thenReturn(Optional.of(playerEntity));

        PlayerRequestDTO conflictRequest = new PlayerRequestDTO(NICKNAME_UPDATED);
        when(repository.existsByNickname(NICKNAME_UPDATED)).thenReturn(true);

        assertThrows(NicknameAlreadyExistsException.class, () -> service.update(ID_EXISTENT, conflictRequest));

        verify(repository, never()).save(any(Player.class));
    }
}
