package com.springboot.match.stats.dtos.map;

import jakarta.validation.constraints.NotNull;

public record GameMapStatusRequestDTO(
        @NotNull
        boolean active
) {}
