package com.thegraid.lobby.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameInstPropsMapperTest {

    private GameInstPropsMapper gameInstPropsMapper;

    @BeforeEach
    public void setUp() {
        gameInstPropsMapper = new GameInstPropsMapperImpl();
    }
}
