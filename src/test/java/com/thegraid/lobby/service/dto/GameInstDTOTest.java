package com.thegraid.lobby.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.thegraid.lobby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GameInstDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GameInstDTO.class);
        GameInstDTO gameInstDTO1 = new GameInstDTO();
        gameInstDTO1.setId(1L);
        GameInstDTO gameInstDTO2 = new GameInstDTO();
        assertThat(gameInstDTO1).isNotEqualTo(gameInstDTO2);
        gameInstDTO2.setId(gameInstDTO1.getId());
        assertThat(gameInstDTO1).isEqualTo(gameInstDTO2);
        gameInstDTO2.setId(2L);
        assertThat(gameInstDTO1).isNotEqualTo(gameInstDTO2);
        gameInstDTO1.setId(null);
        assertThat(gameInstDTO1).isNotEqualTo(gameInstDTO2);
    }
}
