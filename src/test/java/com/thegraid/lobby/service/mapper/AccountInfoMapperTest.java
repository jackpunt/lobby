package com.thegraid.lobby.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountInfoMapperTest {

    private AccountInfoMapper accountInfoMapper;

    @BeforeEach
    public void setUp() {
        accountInfoMapper = new AccountInfoMapperImpl();
    }
}
