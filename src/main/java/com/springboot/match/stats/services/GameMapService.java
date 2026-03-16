package com.springboot.match.stats.services;

import com.springboot.match.stats.dtos.map.GameMapRequestDTO;
import com.springboot.match.stats.dtos.map.GameMapResponseDTO;
import com.springboot.match.stats.dtos.map.GameMapStatusRequestDTO;
import com.springboot.match.stats.models.GameMap;
import com.springboot.match.stats.repositories.GameMapRepository;
import com.springboot.match.stats.services.exceptions.MapAlreadyExistsException;
import com.springboot.match.stats.services.exceptions.ResourceNotFoundException;
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
        validateIfMapAlreadyExists(dto);

        GameMap gameMap = new GameMap();

        gameMap.setName(dto.name());
        gameMap.setActive(dto.active());

        gameMap = repository.save(gameMap);
        return toResponseDTO(gameMap);
    }

    @Transactional
    public GameMapResponseDTO update(Long id, GameMapStatusRequestDTO dto) {
        GameMap gameMap = repository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        updateMapStatus(gameMap, dto);

        gameMap = repository.save(gameMap);
        return toResponseDTO(gameMap);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException();
        }

        repository.deleteById(id);
    }

    private void validateIfMapAlreadyExists(GameMapRequestDTO dto) {
        if (repository.existsByName(dto.name())) {
            throw new MapAlreadyExistsException();
        }
    }

    public void updateMapStatus(GameMap entity, GameMapStatusRequestDTO dto) {
        entity.setActive(dto.active());
    }

    private GameMapResponseDTO toResponseDTO(GameMap gameMap) {
        return new GameMapResponseDTO(
                gameMap.getId(),
                gameMap.getName(),
                gameMap.isActive(),
                gameMap.getCreatedAt()
        );
    }
}
