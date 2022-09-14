package com.thegraid.lobby.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.thegraid.lobby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MemberGamePropsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MemberGamePropsDTO.class);
        MemberGamePropsDTO memberGamePropsDTO1 = new MemberGamePropsDTO();
        memberGamePropsDTO1.setId(1L);
        MemberGamePropsDTO memberGamePropsDTO2 = new MemberGamePropsDTO();
        assertThat(memberGamePropsDTO1).isNotEqualTo(memberGamePropsDTO2);
        memberGamePropsDTO2.setId(memberGamePropsDTO1.getId());
        assertThat(memberGamePropsDTO1).isEqualTo(memberGamePropsDTO2);
        memberGamePropsDTO2.setId(2L);
        assertThat(memberGamePropsDTO1).isNotEqualTo(memberGamePropsDTO2);
        memberGamePropsDTO1.setId(null);
        assertThat(memberGamePropsDTO1).isNotEqualTo(memberGamePropsDTO2);
    }
}
