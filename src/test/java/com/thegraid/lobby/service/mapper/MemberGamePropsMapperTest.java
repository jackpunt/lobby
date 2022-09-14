package com.thegraid.lobby.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberGamePropsMapperTest {

    private MemberGamePropsMapper memberGamePropsMapper;

    @BeforeEach
    public void setUp() {
        memberGamePropsMapper = new MemberGamePropsMapperImpl();
    }
}
