package com.springboot.match.stats.services;

import com.springboot.match.stats.dtos.match.MatchRequestDTO;
import com.springboot.match.stats.dtos.match.MatchResponseDTO;
import com.springboot.match.stats.models.GameMap;
import com.springboot.match.stats.models.Match;
import com.springboot.match.stats.repositories.GameMapRepository;
import com.springboot.match.stats.repositories.MatchRepository;
import com.springboot.match.stats.services.exceptions.InactiveMapException;
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
class MatchServiceTest {
    private static final Long ID_EXISTENT = 1L;
    private static final Long ID_NON_EXISTENT = 99L;
    private static final Long MAP_ID_EXISTENT = 1L;
    private static final String MAP_NAME_EXISTENT = "Train";

    @InjectMocks
    private MatchService service;
    @Mock
    private MatchRepository repository;
    @Mock
    private GameMapRepository gameMapRepository;
    private Match matchEntity;
    private GameMap mapEntity;
    private MatchRequestDTO matchRequest;

    @BeforeEach
    void setUp() {
        mapEntity = new GameMap();
        mapEntity.setId(MAP_ID_EXISTENT);
        mapEntity.setName(MAP_NAME_EXISTENT);
        mapEntity.setActive(true);

        matchEntity = new Match();
        matchEntity.setId(ID_EXISTENT);
        matchEntity.setMap(mapEntity);

        matchRequest = new MatchRequestDTO(MAP_ID_EXISTENT);
    }

    @Test
    void should_return_all_matches() {
        List<Match> list = List.of(matchEntity);
        when(repository.findAll()).thenReturn(list);

        List<MatchResponseDTO> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(matchEntity.getId(), result.get(0).id());
        assertEquals(matchEntity.getMap().getName(), result.get(0).mapName());

        verify(repository, times(1)).findAll();
    }

    @Test
    void should_find_matches_by_id_when_id_exists() {
        when(repository.findById(ID_EXISTENT)).thenReturn(Optional.of(matchEntity));

        MatchResponseDTO result = service.findById(ID_EXISTENT);

        assertNotNull(result);
        assertEquals(matchEntity.getId(), result.id());
        assertEquals(matchEntity.getMap().getName(), result.mapName());

        verify(repository, times(1)).findById(ID_EXISTENT);
    }

    @Test
    void should_insert_matches_and_return_match_response_dto() {
        when(gameMapRepository.findById(MAP_ID_EXISTENT)).thenReturn(Optional.of(mapEntity));
        when(repository.save(any(Match.class))).thenReturn(matchEntity);

        MatchResponseDTO result = service.insert(matchRequest);

        assertNotNull(result);
        assertEquals(matchEntity.getId(), result.id());
        assertEquals(matchEntity.getMap().getName(), result.mapName());

        verify(repository, times(1)).save(any(Match.class));
    }

    @Test
    void should_delete_match_by_id() {
        when(repository.existsById(ID_EXISTENT)).thenReturn(true);

        service.delete(ID_EXISTENT);

        verify(repository, times(1)).deleteById(ID_EXISTENT);
    }

    @Test
    void should_throw_exception_when_id_is_not_deleted() {
        when(repository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(ID_NON_EXISTENT));

        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void should_throw_exception_when_id_is_not_found() {
        when(repository.findById(ID_NON_EXISTENT)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(ID_NON_EXISTENT));

        verify(repository, times(1)).findById(ID_NON_EXISTENT);
    }

    @Test
    void should_throw_exception_when_insert_match_with_inactive_map() {
        mapEntity.setActive(false);

        when(gameMapRepository.findById(MAP_ID_EXISTENT)).thenReturn(Optional.of(mapEntity));

        assertThrows(InactiveMapException.class, () -> service.insert(matchRequest));

        verify(repository, never()).save(any(Match.class));
    }
}
