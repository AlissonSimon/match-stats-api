package com.springboot.match.stats.dtos.stats;

import com.springboot.match.stats.models.enums.ResultType;

public record PlayerMatchStatsResponseDTO(
        Long id,
        Long playerId,
        Long matchId,
        ResultType resultType,
        Integer kills,
        Integer deaths,
        Integer assists,
        Integer headshots
) {}
