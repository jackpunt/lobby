package com.thegraid.lobby.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.thegraid.lobby.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AccountInfoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AccountInfoDTO.class);
        AccountInfoDTO accountInfoDTO1 = new AccountInfoDTO();
        accountInfoDTO1.setId(1L);
        AccountInfoDTO accountInfoDTO2 = new AccountInfoDTO();
        assertThat(accountInfoDTO1).isNotEqualTo(accountInfoDTO2);
        accountInfoDTO2.setId(accountInfoDTO1.getId());
        assertThat(accountInfoDTO1).isEqualTo(accountInfoDTO2);
        accountInfoDTO2.setId(2L);
        assertThat(accountInfoDTO1).isNotEqualTo(accountInfoDTO2);
        accountInfoDTO1.setId(null);
        assertThat(accountInfoDTO1).isNotEqualTo(accountInfoDTO2);
    }
}
