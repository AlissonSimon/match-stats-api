package com.springboot.match.stats.services;

import com.springboot.match.stats.dtos.map.GameMapRequestDTO;
import com.springboot.match.stats.dtos.map.GameMapResponseDTO;
import com.springboot.match.stats.dtos.map.GameMapStatusRequestDTO;
import com.springboot.match.stats.models.GameMap;
import com.springboot.match.stats.repositories.GameMapRepository;
import com.springboot.match.stats.services.exceptions.MapAlreadyExistsException;
import com.springboot.match.stats.services.exceptions.ResourceNotFoundException;
import com.springboot.match.stats.services.exceptions.StatusAlreadySetException;
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
class GameMapServiceTest {
    private static final Long ID_EXISTENT = 1L;
    private static final Long ID_NON_EXISTENT = 99L;
    private static final String NAME_EXISTENT = "Mirage";
    private static final boolean ACTIVE = true;
    private static final boolean INACTIVE = false;

    @InjectMocks
    private GameMapService service;
    @Mock
    private GameMapRepository repository;
    private GameMap gameMapEntity;
    private GameMapRequestDTO gameMapRequest;
    private GameMapStatusRequestDTO gameMapStatusRequest;

    @BeforeEach
    void setUp() {
        gameMapEntity = new GameMap();
        gameMapEntity.setId(ID_EXISTENT);
        gameMapEntity.setName(NAME_EXISTENT);
        gameMapEntity.setActive(ACTIVE);

        gameMapRequest = new GameMapRequestDTO(NAME_EXISTENT, ACTIVE);
        gameMapStatusRequest = new GameMapStatusRequestDTO(ACTIVE);
    }

    @Test
    void should_return_all_maps() {
        List<GameMap> list = List.of(gameMapEntity);
        when(repository.findAll()).thenReturn(list);

        List<GameMapResponseDTO> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(gameMapEntity.getId(), result.get(0).id());
        assertEquals(gameMapEntity.getName(), result.get(0).name());
        assertEquals(gameMapEntity.isActive(), result.get(0).active());

        verify(repository, times(1)).findAll();
    }

    @Test
    void should_find_maps_by_id_when_id_exists() {
        when(repository.findById(ID_EXISTENT)).thenReturn(Optional.of(gameMapEntity));

        GameMapResponseDTO result = service.findById(ID_EXISTENT);

        assertNotNull(result);
        assertEquals(ID_EXISTENT, result.id());
        assertEquals(NAME_EXISTENT, result.name());
        assertEquals(ACTIVE, result.active());

        verify(repository, times(1)).findById(ID_EXISTENT);
    }

    @Test
    void should_insert_maps_and_return_map_response_dto() {
        when(repository.save(any(GameMap.class))).thenReturn(gameMapEntity);

        GameMapResponseDTO result = service.insert(gameMapRequest);

        assertNotNull(result);
        assertEquals(gameMapEntity.getId(), result.id());
        assertEquals(gameMapEntity.getName(), result.name());
        assertEquals(gameMapEntity.isActive(), result.active());

        verify(repository, times(1)).save(any(GameMap.class));
    }

    @Test
    void should_update_map_and_return_map_response_dto() {
        when(repository.findById(ID_EXISTENT)).thenReturn(Optional.of(gameMapEntity));
        when(repository.save(any(GameMap.class))).thenReturn(gameMapEntity);

        GameMapStatusRequestDTO updatedRequest = new GameMapStatusRequestDTO(INACTIVE);
        GameMapResponseDTO updatesResponse = service.update(ID_EXISTENT, updatedRequest);

        assertNotNull(updatesResponse);
        assertEquals(INACTIVE, updatesResponse.active());

        verify(repository, times(1)).findById(ID_EXISTENT);
        verify(repository, times(1)).save(gameMapEntity);
    }

    @Test
    void should_delete_map_when_id_exists() {
        when(repository.existsById(ID_EXISTENT)).thenReturn(true);

        service.delete(ID_EXISTENT);

        verify(repository, times(1)).deleteById(ID_EXISTENT);
    }

    @Test
    void should_throw_exception_when_id_is_not_found() {
        when(repository.findById(ID_NON_EXISTENT)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(ID_NON_EXISTENT));

        verify(repository, times(1)).findById(ID_NON_EXISTENT);
    }

    @Test
    void should_throw_exception_when_updating_non_existent_id() {
        when(repository.findById(ID_NON_EXISTENT)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(ID_NON_EXISTENT, gameMapStatusRequest));

        verify(repository, times(1)).findById(ID_NON_EXISTENT);
        verify(repository, never()).save(any());
    }

    @Test
    void should_throw_exception_when_update_status_is_already_set() {
        when(repository.findById(ID_EXISTENT)).thenReturn(Optional.of(gameMapEntity));

        assertThrows(StatusAlreadySetException.class, () -> service.update(ID_EXISTENT, gameMapStatusRequest));

        verify(repository, times(1)).findById(ID_EXISTENT);
        verify(repository, never()).save(any());
    }

    @Test
    void should_throw_exception_when_id_is_not_deleted() {
        when(repository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(ID_NON_EXISTENT));

        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void should_throw_exception_when_map_already_exists() {
        when(repository.existsByName(NAME_EXISTENT)).thenReturn(true);

        assertThrows(MapAlreadyExistsException.class, () -> service.insert(gameMapRequest));

        verify(repository, times(1)).existsByName(NAME_EXISTENT);
        verify(repository, never()).save(any());
    }
}
