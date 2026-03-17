package com.springboot.match.stats.services;

import com.springboot.match.stats.dtos.match.MatchRequestDTO;
import com.springboot.match.stats.dtos.match.MatchResponseDTO;
import com.springboot.match.stats.models.GameMap;
import com.springboot.match.stats.models.Match;
import com.springboot.match.stats.repositories.GameMapRepository;
import com.springboot.match.stats.repositories.MatchRepository;
import com.springboot.match.stats.services.exceptions.InactiveMapException;
import com.springboot.match.stats.services.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository repository;
    private final GameMapRepository gameMapRepository;

    @Transactional(readOnly = true)
    public List<MatchResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MatchResponseDTO findById(Long id) {
        Match match = repository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        return toResponseDTO(match);
    }

    @Transactional
    public MatchResponseDTO insert(MatchRequestDTO dto) {
        GameMap mapEntity = gameMapRepository.findById(dto.gameMapId())
                .orElseThrow(ResourceNotFoundException::new);

        validateIfMapEntityIsActive(mapEntity);

        Match match = toEntity(mapEntity);

        match = repository.save(match);
        return toResponseDTO(match);
    }

    @Transactional
    public void delete(Long id) {
        validateIfEntityExists(id);

        repository.deleteById(id);
    }

    private void validateIfEntityExists(Long id) {
        if (repository.existsById(id)) {
            throw new ResourceNotFoundException();
        }
    }

    private void validateIfMapEntityIsActive(GameMap entity) {
        if (!entity.isActive()) {
            throw  new InactiveMapException();
        }
    }

    private Match toEntity(GameMap mapEntity) {
        Match entity = new Match();

        entity.setMap(mapEntity);

        return entity;
    }

    private MatchResponseDTO toResponseDTO(Match entity) {
        return new MatchResponseDTO(
                entity.getId(),
                entity.getMap().getName(),
                entity.getPlayedAt()
        );
    }
}
