package com.springboot.match.stats.dtos.stats;

import com.springboot.match.stats.models.enums.ResultType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PlayerMatchStatsRequestDTO(
        @NotNull
        Long playerId,
        @NotNull
        Long matchId,
        @NotNull
        ResultType resultType,
        @NotNull
        @PositiveOrZero
        Integer kills,
        @NotNull
        @PositiveOrZero
        Integer deaths,
        @NotNull
        @PositiveOrZero
        Integer assists,
        @NotNull
        @PositiveOrZero
        Integer headshots
) {}
