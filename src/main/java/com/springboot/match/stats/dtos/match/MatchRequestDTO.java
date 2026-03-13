package com.springboot.match.stats.dtos.match;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MatchRequestDTO(
        @NotBlank
        @Size(min = 3, max = 20)
        String mapName
) {}
