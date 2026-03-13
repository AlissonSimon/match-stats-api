package com.springboot.match.stats.services;

import com.springboot.match.stats.dtos.MatchRequestDTO;
import com.springboot.match.stats.dtos.MatchResponseDTO;
import com.springboot.match.stats.models.Match;
import com.springboot.match.stats.repositories.MatchRepository;
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
    private static final String MAP_NAME_EXISTENT = "Train";

    @InjectMocks
    private MatchService service;
    @Mock
    private MatchRepository repository;
    private Match matchEntity;
    private MatchRequestDTO matchRequest;

    @BeforeEach
    void setUp() {
        matchEntity = new Match();
        matchEntity.setId(ID_EXISTENT);
        matchEntity.setMapName(MAP_NAME_EXISTENT);

        matchRequest = new MatchRequestDTO(MAP_NAME_EXISTENT);
    }

    @Test
    void should_return_all_matches() {
        List<Match> list = List.of(matchEntity);
        when(repository.findAll()).thenReturn(list);

        List<MatchResponseDTO> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(matchEntity.getId(), result.get(0).id());
        assertEquals(matchEntity.getMapName(), result.get(0).mapName());

        verify(repository, times(1)).findAll();
    }

    @Test
    void should_find_matches_by_id_when_id_exists() {
        when(repository.findById(ID_EXISTENT)).thenReturn(Optional.of(matchEntity));

        MatchResponseDTO result = service.findById(ID_EXISTENT);

        assertNotNull(result);
        assertEquals(ID_EXISTENT, result.id());
        assertEquals(MAP_NAME_EXISTENT, result.mapName());

        verify(repository, times(1)).findById(ID_EXISTENT);
    }

    @Test
    void should_insert_matches_and_return_match_response_dto() {
        when(repository.save(any(Match.class))).thenReturn(matchEntity);

        MatchResponseDTO result = service.insert(matchRequest);

        assertNotNull(result);
        assertEquals(matchEntity.getId(), result.id());
        assertEquals(matchEntity.getMapName(), result.mapName());

        verify(repository, times(1)).save(any(Match.class));
    }

    @Test
    void should_throw_exception_when_id_is_not_found() {
        when(repository.findById(ID_NON_EXISTENT)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(ID_NON_EXISTENT));

        verify(repository, times(1)).findById(ID_NON_EXISTENT);
    }
}
