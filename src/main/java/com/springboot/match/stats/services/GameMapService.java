package com.springboot.match.stats.services;

import com.springboot.match.stats.dtos.map.GameMapRequestDTO;
import com.springboot.match.stats.dtos.map.GameMapResponseDTO;
import com.springboot.match.stats.dtos.map.GameMapStatusRequestDTO;
import com.springboot.match.stats.models.GameMap;
import com.springboot.match.stats.repositories.GameMapRepository;
import com.springboot.match.stats.services.exceptions.MapAlreadyExistsException;
import com.springboot.match.stats.services.exceptions.ResourceNotFoundException;
import com.springboot.match.stats.services.exceptions.StatusAlreadySetException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameMapService {
    private final GameMapRepository repository;

    @Transactional(readOnly = true)
    public List<GameMapResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public GameMapResponseDTO findById(Long id) {
        GameMap gameMap = repository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        return toResponseDTO(gameMap);
    }

    @Transactional
    public GameMapResponseDTO insert(GameMapRequestDTO dto) {
        validateIfEntityNameAlreadyExists(dto);

        GameMap gameMap = toEntity(dto);

        gameMap = repository.save(gameMap);
        return toResponseDTO(gameMap);
    }

    @Transactional
    public GameMapResponseDTO update(Long id, GameMapStatusRequestDTO dto) {
        GameMap gameMap = repository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        validateIfEntityStatusIsAlreadySet(gameMap, dto);

        updateEntityStatus(gameMap, dto);

        gameMap = repository.save(gameMap);
        return toResponseDTO(gameMap);
    }

    @Transactional
    public void delete(Long id) {
        validateIfEntityExists(id);

        repository.deleteById(id);
    }

    private void validateIfEntityExists(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException();
        }
    }

    private void validateIfEntityNameAlreadyExists(GameMapRequestDTO dto) {
        if (repository.existsByName(dto.name())) {
            throw new MapAlreadyExistsException();
        }
    }

    private void validateIfEntityStatusIsAlreadySet(GameMap entity, GameMapStatusRequestDTO dto) {
        if (entity.isActive() == dto.active()) {
            throw new StatusAlreadySetException();
        }
    }

    private void updateEntityStatus(GameMap entity, GameMapStatusRequestDTO dto) {
        entity.setActive(dto.active());
    }

    private GameMap toEntity(GameMapRequestDTO dto) {
        GameMap entity = new GameMap();

        entity.setName(dto.name());
        entity.setActive(dto.active());

        return entity;
    }

    private GameMapResponseDTO toResponseDTO(GameMap entity) {
        return new GameMapResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
