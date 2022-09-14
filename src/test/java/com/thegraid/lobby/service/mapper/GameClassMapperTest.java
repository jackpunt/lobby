package com.thegraid.lobby.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameClassMapperTest {

    private GameClassMapper gameClassMapper;

    @BeforeEach
    public void setUp() {
        gameClassMapper = new GameClassMapperImpl();
    }
}
