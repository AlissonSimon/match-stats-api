package com.springboot.match.stats.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResultType {
    VICTORY(25),
    DEFEAT(-25),
    TIE(0);

    private final int eloChange;
}
