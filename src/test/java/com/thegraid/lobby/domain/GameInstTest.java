package com.thegraid.lobby.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thegraid.lobby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GameInstTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GameInst.class);
        GameInst gameInst1 = new GameInst();
        gameInst1.setId(1L);
        GameInst gameInst2 = new GameInst();
        gameInst2.setId(gameInst1.getId());
        assertThat(gameInst1).isEqualTo(gameInst2);
        gameInst2.setId(2L);
        assertThat(gameInst1).isNotEqualTo(gameInst2);
        gameInst1.setId(null);
        assertThat(gameInst1).isNotEqualTo(gameInst2);
    }
}
