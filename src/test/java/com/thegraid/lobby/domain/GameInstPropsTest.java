package com.thegraid.lobby.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thegraid.lobby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GameInstPropsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GameInstProps.class);
        GameInstProps gameInstProps1 = new GameInstProps();
        gameInstProps1.setId(1L);
        GameInstProps gameInstProps2 = new GameInstProps();
        gameInstProps2.setId(gameInstProps1.getId());
        assertThat(gameInstProps1).isEqualTo(gameInstProps2);
        gameInstProps2.setId(2L);
        assertThat(gameInstProps1).isNotEqualTo(gameInstProps2);
        gameInstProps1.setId(null);
        assertThat(gameInstProps1).isNotEqualTo(gameInstProps2);
    }
}
