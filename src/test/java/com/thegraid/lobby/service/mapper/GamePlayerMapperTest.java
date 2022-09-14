package com.thegraid.lobby.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GamePlayerMapperTest {

    private GamePlayerMapper gamePlayerMapper;

    @BeforeEach
    public void setUp() {
        gamePlayerMapper = new GamePlayerMapperImpl();
    }
}
