package com.springboot.match.stats.services;

import com.springboot.match.stats.dtos.player.PlayerRequestDTO;
import com.springboot.match.stats.dtos.player.PlayerResponseDTO;
import com.springboot.match.stats.services.exceptions.NicknameAlreadyExistsException;
import com.springboot.match.stats.services.exceptions.ResourceNotFoundException;
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
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlayerResponseDTO findById(Long id) {
        Player player = repository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        return toResponseDTO(player);
    }

    @Transactional
    public PlayerResponseDTO insert(PlayerRequestDTO dto) {
        validateIfEntityNicknameExists(dto);

        Player player = toEntity(dto);

        player = repository.save(player);
        return toResponseDTO(player);
    }

    @Transactional
    public PlayerResponseDTO update(Long id, PlayerRequestDTO dto) {
        Player player = repository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        if (!player.getNickname().equals(dto.nickname())) {
            validateIfEntityNicknameExists(dto);
        }

        updateEntityData(player, dto);

        player = repository.save(player);
        return toResponseDTO(player);
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    @Transactional
    public void delete(Long id) {
        validateIfEntityExists(id);

        repository.deleteById(id);
    }

    private void updateEntityData(Player entity, PlayerRequestDTO dto) {
        entity.setNickname(dto.nickname());
    }

    private void validateIfEntityExists(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException();
        }
    }

    private void validateIfEntityNicknameExists(PlayerRequestDTO dto) {
        if (repository.existsByNickname(dto.nickname())) {
            throw new NicknameAlreadyExistsException();
        }
    }

    private Player toEntity(PlayerRequestDTO dto) {
        Player entity = new Player();

        entity.setNickname(dto.nickname());
        entity.setElo(DEFAULT_ELO);

        return entity;
    }

    private PlayerResponseDTO toResponseDTO(Player entity) {
        return new PlayerResponseDTO(
                entity.getId(),
                entity.getNickname(),
                entity.getElo(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
