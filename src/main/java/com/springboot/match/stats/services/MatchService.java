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
        Match match = new Match();

        GameMap mapEntity = gameMapRepository.findById(dto.gameMapId())
                .orElseThrow(ResourceNotFoundException::new);

        if (!mapEntity.isActive()) {
            throw new InactiveMapException();
        }

        match.setMap(mapEntity);

        match = repository.save(match);
        return toResponseDTO(match);
    }

    private MatchResponseDTO toResponseDTO(Match match) {
        return new MatchResponseDTO(
                match.getId(),
                match.getMap().getName(),
                match.getPlayedAt()
        );
    }
}
