package com.springboot.match.stats.dtos.match;

import java.time.LocalDateTime;

public record MatchResponseDTO (
        Long id,
        String mapName,
        LocalDateTime playedAt
) {}
