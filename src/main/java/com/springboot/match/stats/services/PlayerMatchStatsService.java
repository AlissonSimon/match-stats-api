package com.springboot.match.stats.services;

import com.springboot.match.stats.dtos.stats.PlayerMatchStatsRequestDTO;
import com.springboot.match.stats.dtos.stats.PlayerMatchStatsResponseDTO;
import com.springboot.match.stats.models.Match;
import com.springboot.match.stats.models.Player;
import com.springboot.match.stats.models.PlayerMatchStats;
import com.springboot.match.stats.repositories.MatchRepository;
import com.springboot.match.stats.repositories.PlayerMatchStatsRepository;
import com.springboot.match.stats.repositories.PlayerRepository;
import com.springboot.match.stats.services.exceptions.HeadshotsExceedKillsException;
import com.springboot.match.stats.services.exceptions.PlayerAlreadyHasStatsException;
import com.springboot.match.stats.services.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerMatchStatsService {
    private final PlayerMatchStatsRepository statsRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    @Transactional(readOnly = true)
    public List<PlayerMatchStatsResponseDTO> findAll() {
        return statsRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlayerMatchStatsResponseDTO findById(Long id) {
        PlayerMatchStats playerStats = statsRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        return toResponseDTO(playerStats);
    }

    @Transactional
    public PlayerMatchStatsResponseDTO insert(PlayerMatchStatsRequestDTO dto) {
        validateIfPlayerAndMatchExists(dto.playerId(), dto.matchId());
        validateIfPlayerAlreadyHasStatsInTheMatch(dto);
        validateHeadshotLimit(dto);

        PlayerMatchStats stats = toEntity(dto);

        stats = statsRepository.save(stats);
        return toResponseDTO(stats);
    }

    @Transactional
    public void delete(Long id) {
        if (!statsRepository.existsById(id)) {
            throw new ResourceNotFoundException();
        }

        statsRepository.deleteById(id);
    }

    private void validateIfPlayerAlreadyHasStatsInTheMatch(PlayerMatchStatsRequestDTO dto) {
        if (statsRepository.existsByPlayerAndMatchId(dto.playerId(), dto.matchId())) {
            throw new PlayerAlreadyHasStatsException();
        }
    }

    private void validateIfPlayerAndMatchExists(Long playerId, Long matchId) {
        if (!playerRepository.existsById(playerId)) {
            throw new ResourceNotFoundException();
        }

        if (!matchRepository.existsById(matchId)) {
            throw new ResourceNotFoundException();
        }
    }

    private void validateHeadshotLimit(PlayerMatchStatsRequestDTO dto) {
        if (dto.headshots() > dto.kills()) {
            throw new HeadshotsExceedKillsException();
        }
    }

    private PlayerMatchStats toEntity(PlayerMatchStatsRequestDTO dto) {
        PlayerMatchStats entity = new PlayerMatchStats();

        Player player = playerRepository.findById(dto.playerId()).get();
        Match match = matchRepository.findById(dto.matchId()).get();

        entity.setPlayer(player);
        entity.setMatch(match);
        entity.setResultType(dto.resultType());
        entity.setKills(dto.kills());
        entity.setDeaths(dto.deaths());
        entity.setAssists(dto.assists());
        entity.setHeadshots(dto.headshots());

        return entity;
    }

    private PlayerMatchStatsResponseDTO toResponseDTO(PlayerMatchStats playerStats) {
        return new PlayerMatchStatsResponseDTO(
                playerStats.getId(),
                playerStats.getPlayer().getId(),
                playerStats.getMatch().getId(),
                playerStats.getResultType(),
                playerStats.getKills(),
                playerStats.getDeaths(),
                playerStats.getAssists(),
                playerStats.getHeadshots(),
                playerStats.getKillDeathRatio(),
                playerStats.getHeadshotPercentage()
        );
    }
}
