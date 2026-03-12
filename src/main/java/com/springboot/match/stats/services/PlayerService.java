package com.springboot.match.stats.services;

import com.springboot.match.stats.dtos.PlayerRequestDTO;
import com.springboot.match.stats.dtos.PlayerResponseDTO;
import com.springboot.match.stats.models.Player;
import com.springboot.match.stats.repositories.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private static final int DEFAULT_ELO = 1000;

    private final PlayerRepository repository;

    @Transactional(readOnly = true)
    public List<PlayerResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(player -> new PlayerResponseDTO(player.getId(), player.getNickname(), player.getElo(), player.getCreatedAt(), player.getUpdatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public PlayerResponseDTO findById(Long id) {
        Player player = repository.findById(id)
                .orElseThrow(() -> new RuntimeException());
        return toResponseDTO(player);
    }

    @Transactional
    public PlayerResponseDTO insert(PlayerRequestDTO dto) {
        Player player = new Player();

        player.setNickname(dto.nickname());
        player.setElo(DEFAULT_ELO);

        player = repository.save(player);
        return toResponseDTO(player);
    }

    @Transactional
    public PlayerResponseDTO update(Long id, PlayerRequestDTO dto) {
        Player player = repository.getReferenceById(id);
        updateData(player, dto);

        player = repository.save(player);
        return toResponseDTO(player);
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("ID não encontrado");
        }

        repository.deleteById(id);
    }

    public void updateData(Player entity, PlayerRequestDTO dto) {
        entity.setNickname(dto.nickname());
    }

    public PlayerResponseDTO toResponseDTO(Player player) {
        return new PlayerResponseDTO(
                player.getId(),
                player.getNickname(),
                player.getElo(),
                player.getCreatedAt(),
                player.getUpdatedAt()
        );
    }
}
