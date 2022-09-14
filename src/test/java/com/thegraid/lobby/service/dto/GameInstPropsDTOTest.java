package com.thegraid.lobby.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.thegraid.lobby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GameInstPropsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GameInstPropsDTO.class);
        GameInstPropsDTO gameInstPropsDTO1 = new GameInstPropsDTO();
        gameInstPropsDTO1.setId(1L);
        GameInstPropsDTO gameInstPropsDTO2 = new GameInstPropsDTO();
        assertThat(gameInstPropsDTO1).isNotEqualTo(gameInstPropsDTO2);
        gameInstPropsDTO2.setId(gameInstPropsDTO1.getId());
        assertThat(gameInstPropsDTO1).isEqualTo(gameInstPropsDTO2);
        gameInstPropsDTO2.setId(2L);
        assertThat(gameInstPropsDTO1).isNotEqualTo(gameInstPropsDTO2);
        gameInstPropsDTO1.setId(null);
        assertThat(gameInstPropsDTO1).isNotEqualTo(gameInstPropsDTO2);
    }
}
