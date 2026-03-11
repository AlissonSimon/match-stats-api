package com.springboot.match.stats.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlayerRequestDTO(
        @NotBlank
        @Size(min = 3, max = 20)
        String nickname
) {}
