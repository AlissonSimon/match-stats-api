package com.springboot.match.stats.dtos.map;

import java.time.LocalDateTime;

public record GameMapResponseDTO(
        Long id,
        String name,
        boolean isActive,
        LocalDateTime createdAt
) {}
