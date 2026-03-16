package com.springboot.match.stats.dtos.match;

import jakarta.validation.constraints.NotNull;

public record MatchRequestDTO(
        @NotNull
        Long gameMapId
) {}
