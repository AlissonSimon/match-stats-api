package com.springboot.match.stats.services;

import com.springboot.match.stats.dtos.stats.PlayerMatchStatsRequestDTO;
import com.springboot.match.stats.dtos.stats.PlayerMatchStatsResponseDTO;
import com.springboot.match.stats.models.GameMap;
import com.springboot.match.stats.models.Match;
import com.springboot.match.stats.models.Player;
import com.springboot.match.stats.models.PlayerMatchStats;
import com.springboot.match.stats.models.enums.ResultType;
import com.springboot.match.stats.repositories.MatchRepository;
import com.springboot.match.stats.repositories.PlayerMatchStatsRepository;
import com.springboot.match.stats.repositories.PlayerRepository;
import com.springboot.match.stats.services.exceptions.HeadshotsExceedKillsException;
import com.springboot.match.stats.services.exceptions.PlayerAlreadyHasStatsException;
import com.springboot.match.stats.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlayerMatchStatsServiceTest {
    private static final Long ID_EXISTENT = 1L;
    private static final Long ID_NON_EXISTENT = 99L;
    private static final String PLAYER_NAME = "Fallen";
    private static final Integer PLAYER_DEFAULT_ELO = 1000;
    private static final String MAP_NAME = "TRAIN";
    private static final Integer VALID_KILLS_STATS = 25;
    private static final Integer VALID_DEATHS_STATS = 10;
    private static final Integer VALID_ASSISTS_STATS = 15;
    private static final Integer VALID_HEADSHOTS_STATS = 18;
    private static final Integer INVALID_HEADSHOTS_STATS = 30;

    @InjectMocks
    private PlayerMatchStatsService service;
    @Mock
    private PlayerMatchStatsRepository statsRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private MatchRepository matchRepository;
    private PlayerMatchStats statsEntity;
    private Player playerEntity;
    private Match matchEntity;
    private GameMap mapEntity;
    private PlayerMatchStatsRequestDTO statsRequest;

    @BeforeEach
    void setUp() {
        playerEntity = new Player();
        playerEntity.setId(ID_EXISTENT);
        playerEntity.setNickname(PLAYER_NAME);
        playerEntity.setElo(PLAYER_DEFAULT_ELO);

        mapEntity = new GameMap();
        mapEntity.setId(ID_EXISTENT);
        mapEntity.setName(MAP_NAME);
        mapEntity.setActive(true);

        matchEntity = new Match();
        matchEntity.setId(ID_EXISTENT);
        matchEntity.setMap(mapEntity);

        statsEntity = new PlayerMatchStats();
        statsEntity.setId(ID_EXISTENT);
        statsEntity.setPlayer(playerEntity);
        statsEntity.setMatch(matchEntity);
        statsEntity.setResultType(ResultType.VICTORY);
        statsEntity.setKills(VALID_KILLS_STATS);
        statsEntity.setDeaths(VALID_DEATHS_STATS);
        statsEntity.setAssists(VALID_ASSISTS_STATS);
        statsEntity.setHeadshots(VALID_HEADSHOTS_STATS);

        statsRequest = new PlayerMatchStatsRequestDTO(ID_EXISTENT, ID_EXISTENT, ResultType.VICTORY, VALID_KILLS_STATS, VALID_DEATHS_STATS, VALID_ASSISTS_STATS, VALID_HEADSHOTS_STATS);
    }

    @Test
    void should_return_all_stats() {
        List<PlayerMatchStats> list = List.of(statsEntity);
        when(statsRepository.findAll()).thenReturn(list);

        List<PlayerMatchStatsResponseDTO> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(statsEntity.getId(), result.get(0).id());
        assertEquals(statsEntity.getPlayer().getId(), result.get(0).playerId());
        assertEquals(statsEntity.getMatch().getId(), result.get(0).matchId());
        assertEquals(statsEntity.getResultType(), result.get(0).resultType());
        assertEquals(statsEntity.getKills(), result.get(0).kills());
        assertEquals(statsEntity.getDeaths(), result.get(0).deaths());
        assertEquals(statsEntity.getAssists(), result.get(0).assists());
        assertEquals(statsEntity.getHeadshots(), result.get(0).headshots());
        assertEquals(statsEntity.getKillDeathRatio(), result.get(0).killDeathRatio());
        assertEquals(statsEntity.getHeadshotPercentage(), result.get(0).headshotPercentage());

        verify(statsRepository, times(1)).findAll();
    }

    @Test
    void should_find_stats_by_id_when_id_exists() {
        when(statsRepository.findById(ID_EXISTENT)).thenReturn(Optional.of(statsEntity));

        PlayerMatchStatsResponseDTO result = service.findById(ID_EXISTENT);

        assertNotNull(result);
        assertEquals(statsEntity.getId(), result.id());
        assertEquals(statsEntity.getPlayer().getId(), result.playerId());
        assertEquals(statsEntity.getMatch().getId(), result.matchId());
        assertEquals(statsEntity.getResultType(), result.resultType());
        assertEquals(statsEntity.getKills(), result.kills());
        assertEquals(statsEntity.getDeaths(), result.deaths());
        assertEquals(statsEntity.getAssists(), result.assists());
        assertEquals(statsEntity.getHeadshots(), result.headshots());
        assertEquals(statsEntity.getKillDeathRatio(), result.killDeathRatio());
        assertEquals(statsEntity.getHeadshotPercentage(), result.headshotPercentage());

        verify(statsRepository, times(1)).findById(ID_EXISTENT);
    }

    @Test
    void should_insert_stats_and_return_stats_response_dto() {
        when(playerRepository.existsById(ID_EXISTENT)).thenReturn(true);
        when(matchRepository.existsById(ID_EXISTENT)).thenReturn(true);

        when(playerRepository.findById(ID_EXISTENT)).thenReturn(Optional.of(playerEntity));
        when(matchRepository.findById(ID_EXISTENT)).thenReturn(Optional.of(matchEntity));

        when(statsRepository.existsByPlayerIdAndMatchId(ID_EXISTENT, ID_EXISTENT)).thenReturn(false);
        when(statsRepository.save(any(PlayerMatchStats.class))).thenReturn(statsEntity);

        PlayerMatchStatsResponseDTO result = service.insert(statsRequest);

        assertNotNull(result);
        assertEquals(statsEntity.getId(), result.id());
        assertEquals(statsEntity.getPlayer().getId(), result.playerId());
        assertEquals(statsEntity.getMatch().getId(), result.matchId());
        assertEquals(statsEntity.getResultType(), result.resultType());
        assertEquals(statsEntity.getKills(), result.kills());
        assertEquals(statsEntity.getDeaths(), result.deaths());
        assertEquals(statsEntity.getAssists(), result.assists());
        assertEquals(statsEntity.getHeadshots(), result.headshots());
        assertEquals(statsEntity.getKillDeathRatio(), result.killDeathRatio());
        assertEquals(statsEntity.getHeadshotPercentage(), result.headshotPercentage());

        verify(statsRepository, times(1)).save(any(PlayerMatchStats.class));
    }

    @Test
    void should_delete_stats_by_id() {
        when(statsRepository.existsById(ID_EXISTENT)).thenReturn(true);

        service.delete(ID_EXISTENT);

        verify(statsRepository, times(1)).deleteById(ID_EXISTENT);
    }

    @Test
    void should_increase_player_elo_when_result_is_victory() {
        when(playerRepository.existsById(ID_EXISTENT)).thenReturn(true);
        when(matchRepository.existsById(ID_EXISTENT)).thenReturn(true);
        when(statsRepository.existsByPlayerIdAndMatchId(anyLong(), anyLong())).thenReturn(false);

        when(playerRepository.findById(ID_EXISTENT)).thenReturn(Optional.of(playerEntity));
        when(matchRepository.findById(ID_EXISTENT)).thenReturn(Optional.of(matchEntity));

        when(statsRepository.save(any(PlayerMatchStats.class))).thenReturn(statsEntity);

        service.insert(statsRequest);

        ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);

        verify(playerRepository).save(playerCaptor.capture());

        Player savedPlayer = playerCaptor.getValue();

        int expectedElo = PLAYER_DEFAULT_ELO + ResultType.VICTORY.getEloChange();

        assertEquals(expectedElo, savedPlayer.getElo());
        assertEquals(PLAYER_NAME, savedPlayer.getNickname());
    }

    @Test
    void should_not_allow_player_elo_to_drop_below_zero() {
        playerEntity.setElo(5);

        statsEntity.setResultType(ResultType.DEFEAT);

        PlayerMatchStatsRequestDTO defeatRequest = new PlayerMatchStatsRequestDTO(
                ID_EXISTENT,
                ID_EXISTENT,
                ResultType.DEFEAT,
                VALID_KILLS_STATS,
                VALID_DEATHS_STATS,
                VALID_ASSISTS_STATS,
                VALID_HEADSHOTS_STATS);

        when(playerRepository.existsById(ID_EXISTENT)).thenReturn(true);
        when(matchRepository.existsById(ID_EXISTENT)).thenReturn(true);
        when(statsRepository.existsByPlayerIdAndMatchId(anyLong(), anyLong())).thenReturn(false);

        when(playerRepository.findById(ID_EXISTENT)).thenReturn(Optional.of(playerEntity));
        when(matchRepository.findById(ID_EXISTENT)).thenReturn(Optional.of(matchEntity));

        when(statsRepository.save(any(PlayerMatchStats.class))).thenReturn(statsEntity);

        service.insert(defeatRequest);

        assertEquals(0, playerEntity.getElo());

        verify(playerRepository).save(playerEntity);
    }

    @Test
    void should_throw_exception_when_id_is_not_found() {
        when(statsRepository.findById(ID_NON_EXISTENT)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(ID_NON_EXISTENT));

        verify(statsRepository, times(1)).findById(ID_NON_EXISTENT);
    }

    @Test
    void should_throw_exception_when_id_is_not_deleted() {
        when(statsRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(ID_NON_EXISTENT));

        verify(statsRepository, never()).deleteById(ID_NON_EXISTENT);
    }

    @Test
    void should_throw_exception_when_player_id_and_match_id_do_not_exist() {
        assertThrows(ResourceNotFoundException.class, () -> service.insert(statsRequest));

        verify(statsRepository, never()).save(any(PlayerMatchStats.class));
    }

    @Test
    void should_throw_exception_when_player_already_has_stats() {
        when(playerRepository.existsById(ID_EXISTENT)).thenReturn(true);
        when(matchRepository.existsById(ID_EXISTENT)).thenReturn(true);

        when(statsRepository.existsByPlayerIdAndMatchId(ID_EXISTENT, ID_EXISTENT)).thenReturn(true);

        assertThrows(PlayerAlreadyHasStatsException.class, () -> service.insert(statsRequest));

        verify(statsRepository, never()).save(any(PlayerMatchStats.class));
    }

    @Test
    void should_throw_exception_when_headshots_exceeds_kills() {
        when(playerRepository.existsById(anyLong())).thenReturn(true);
        when(matchRepository.existsById(anyLong())).thenReturn(true);
        when(statsRepository.existsByPlayerIdAndMatchId(anyLong(), anyLong())).thenReturn(false);

        PlayerMatchStatsRequestDTO invalidRequest = new PlayerMatchStatsRequestDTO(
                ID_EXISTENT,
                ID_EXISTENT,
                ResultType.VICTORY,
                VALID_KILLS_STATS,
                VALID_DEATHS_STATS,
                VALID_ASSISTS_STATS,
                INVALID_HEADSHOTS_STATS);

        assertThrows(HeadshotsExceedKillsException.class, () -> service.insert(invalidRequest));

        verify(statsRepository, never()).save(any(PlayerMatchStats.class));
    }
}
