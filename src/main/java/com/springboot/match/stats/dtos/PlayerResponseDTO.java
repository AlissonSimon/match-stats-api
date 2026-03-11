package com.springboot.match.stats.dtos;

import java.time.LocalDateTime;

public record PlayerResponseDTO(
        Long id,
        String nickname,
        Integer elo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
