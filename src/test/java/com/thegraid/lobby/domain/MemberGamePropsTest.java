package com.thegraid.lobby.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thegraid.lobby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MemberGamePropsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MemberGameProps.class);
        MemberGameProps memberGameProps1 = new MemberGameProps();
        memberGameProps1.setId(1L);
        MemberGameProps memberGameProps2 = new MemberGameProps();
        memberGameProps2.setId(memberGameProps1.getId());
        assertThat(memberGameProps1).isEqualTo(memberGameProps2);
        memberGameProps2.setId(2L);
        assertThat(memberGameProps1).isNotEqualTo(memberGameProps2);
        memberGameProps1.setId(null);
        assertThat(memberGameProps1).isNotEqualTo(memberGameProps2);
    }
}
