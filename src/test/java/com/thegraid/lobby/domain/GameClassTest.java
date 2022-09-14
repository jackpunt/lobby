package com.thegraid.lobby.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thegraid.lobby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GameClassTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GameClass.class);
        GameClass gameClass1 = new GameClass();
        gameClass1.setId(1L);
        GameClass gameClass2 = new GameClass();
        gameClass2.setId(gameClass1.getId());
        assertThat(gameClass1).isEqualTo(gameClass2);
        gameClass2.setId(2L);
        assertThat(gameClass1).isNotEqualTo(gameClass2);
        gameClass1.setId(null);
        assertThat(gameClass1).isNotEqualTo(gameClass2);
    }
}
