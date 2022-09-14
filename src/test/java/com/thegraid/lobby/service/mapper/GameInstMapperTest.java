package com.thegraid.lobby.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameInstMapperTest {

    private GameInstMapper gameInstMapper;

    @BeforeEach
    public void setUp() {
        gameInstMapper = new GameInstMapperImpl();
    }
}
