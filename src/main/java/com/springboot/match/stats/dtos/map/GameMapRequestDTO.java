package com.springboot.match.stats.dtos.map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GameMapRequestDTO(
        @NotBlank
        @Size(min = 3, max = 20)
        String name,
        boolean isActive
) {}
