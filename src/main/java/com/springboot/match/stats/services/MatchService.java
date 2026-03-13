package com.springboot.match.stats.services;

import com.springboot.match.stats.dtos.MatchRequestDTO;
import com.springboot.match.stats.dtos.MatchResponseDTO;
import com.springboot.match.stats.models.Match;
import com.springboot.match.stats.repositories.MatchRepository;
import com.springboot.match.stats.services.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository repository;

    @Transactional(readOnly = true)
    public List<MatchResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(match -> new MatchResponseDTO(match.getId(), match.getMapName(), match.getPlayedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public MatchResponseDTO findById(Long id) {
        Match match = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException());
        return toResponseDTO(match);
    }

    @Transactional
    public MatchResponseDTO insert(MatchRequestDTO dto) {
        Match match = new Match();

        match.setMapName(dto.mapName());

        repository.save(match);
        return toResponseDTO(match);
    }

    private MatchResponseDTO toResponseDTO(Match match) {
        return new MatchResponseDTO(
                match.getId(),
                match.getMapName(),
                match.getPlayedAt()
        );
    }
}
