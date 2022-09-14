package com.thegraid.lobby.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.thegraid.lobby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GameClassDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GameClassDTO.class);
        GameClassDTO gameClassDTO1 = new GameClassDTO();
        gameClassDTO1.setId(1L);
        GameClassDTO gameClassDTO2 = new GameClassDTO();
        assertThat(gameClassDTO1).isNotEqualTo(gameClassDTO2);
        gameClassDTO2.setId(gameClassDTO1.getId());
        assertThat(gameClassDTO1).isEqualTo(gameClassDTO2);
        gameClassDTO2.setId(2L);
        assertThat(gameClassDTO1).isNotEqualTo(gameClassDTO2);
        gameClassDTO1.setId(null);
        assertThat(gameClassDTO1).isNotEqualTo(gameClassDTO2);
    }
}
