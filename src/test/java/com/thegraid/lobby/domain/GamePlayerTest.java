package com.thegraid.lobby.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thegraid.lobby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GamePlayerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GamePlayer.class);
        GamePlayer gamePlayer1 = new GamePlayer();
        gamePlayer1.setId(1L);
        GamePlayer gamePlayer2 = new GamePlayer();
        gamePlayer2.setId(gamePlayer1.getId());
        assertThat(gamePlayer1).isEqualTo(gamePlayer2);
        gamePlayer2.setId(2L);
        assertThat(gamePlayer1).isNotEqualTo(gamePlayer2);
        gamePlayer1.setId(null);
        assertThat(gamePlayer1).isNotEqualTo(gamePlayer2);
    }
}
