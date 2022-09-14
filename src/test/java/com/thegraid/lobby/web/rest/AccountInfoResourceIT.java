package com.thegraid.lobby.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thegraid.lobby.IntegrationTest;
import com.thegraid.lobby.domain.AccountInfo;
import com.thegraid.lobby.domain.User;
import com.thegraid.lobby.repository.AccountInfoRepository;
import com.thegraid.lobby.service.dto.AccountInfoDTO;
import com.thegraid.lobby.service.mapper.AccountInfoMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AccountInfoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AccountInfoResourceIT {

    private static final Integer DEFAULT_VERSION = 1;
    private static final Integer UPDATED_VERSION = 2;

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/account-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @Autowired
    private AccountInfoMapper accountInfoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAccountInfoMockMvc;

    private AccountInfo accountInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccountInfo createEntity(EntityManager em) {
        AccountInfo accountInfo = new AccountInfo().version(DEFAULT_VERSION).type(DEFAULT_TYPE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        accountInfo.setUser(user);
        return accountInfo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccountInfo createUpdatedEntity(EntityManager em) {
        AccountInfo accountInfo = new AccountInfo().version(UPDATED_VERSION).type(UPDATED_TYPE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        accountInfo.setUser(user);
        return accountInfo;
    }

    @BeforeEach
    public void initTest() {
        accountInfo = createEntity(em);
    }

    @Test
    @Transactional
    void createAccountInfo() throws Exception {
        int databaseSizeBeforeCreate = accountInfoRepository.findAll().size();
        // Create the AccountInfo
        AccountInfoDTO accountInfoDTO = accountInfoMapper.toDto(accountInfo);
        restAccountInfoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountInfoDTO))
            )
            .andExpect(status().isCreated());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeCreate + 1);
        AccountInfo testAccountInfo = accountInfoList.get(accountInfoList.size() - 1);
        assertThat(testAccountInfo.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testAccountInfo.getType()).isEqualTo(DEFAULT_TYPE);

        // Validate the id for MapsId, the ids must be same
        assertThat(testAccountInfo.getId()).isEqualTo(accountInfoDTO.getUser().getId());
    }

    @Test
    @Transactional
    void createAccountInfoWithExistingId() throws Exception {
        // Create the AccountInfo with an existing ID
        accountInfo.setId(1L);
        AccountInfoDTO accountInfoDTO = accountInfoMapper.toDto(accountInfo);

        int databaseSizeBeforeCreate = accountInfoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAccountInfoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateAccountInfoMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        accountInfoRepository.saveAndFlush(accountInfo);
        int databaseSizeBeforeCreate = accountInfoRepository.findAll().size();
        // Add a new parent entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();

        // Load the accountInfo
        AccountInfo updatedAccountInfo = accountInfoRepository.findById(accountInfo.getId()).get();
        assertThat(updatedAccountInfo).isNotNull();
        // Disconnect from session so that the updates on updatedAccountInfo are not directly saved in db
        em.detach(updatedAccountInfo);

        // Update the User with new association value
        updatedAccountInfo.setUser(user);
        AccountInfoDTO updatedAccountInfoDTO = accountInfoMapper.toDto(updatedAccountInfo);
        assertThat(updatedAccountInfoDTO).isNotNull();

        // Update the entity
        restAccountInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAccountInfoDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAccountInfoDTO))
            )
            .andExpect(status().isOk());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeCreate);
        AccountInfo testAccountInfo = accountInfoList.get(accountInfoList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testAccountInfo.getId()).isEqualTo(testAccountInfo.getUser().getId());
    }

    @Test
    @Transactional
    void getAllAccountInfos() throws Exception {
        // Initialize the database
        accountInfoRepository.saveAndFlush(accountInfo);

        // Get all the accountInfoList
        restAccountInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(accountInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));
    }

    @Test
    @Transactional
    void getAccountInfo() throws Exception {
        // Initialize the database
        accountInfoRepository.saveAndFlush(accountInfo);

        // Get the accountInfo
        restAccountInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, accountInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(accountInfo.getId().intValue()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE));
    }

    @Test
    @Transactional
    void getNonExistingAccountInfo() throws Exception {
        // Get the accountInfo
        restAccountInfoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAccountInfo() throws Exception {
        // Initialize the database
        accountInfoRepository.saveAndFlush(accountInfo);

        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();

        // Update the accountInfo
        AccountInfo updatedAccountInfo = accountInfoRepository.findById(accountInfo.getId()).get();
        // Disconnect from session so that the updates on updatedAccountInfo are not directly saved in db
        em.detach(updatedAccountInfo);
        updatedAccountInfo.version(UPDATED_VERSION).type(UPDATED_TYPE);
        AccountInfoDTO accountInfoDTO = accountInfoMapper.toDto(updatedAccountInfo);

        restAccountInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, accountInfoDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountInfoDTO))
            )
            .andExpect(status().isOk());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
        AccountInfo testAccountInfo = accountInfoList.get(accountInfoList.size() - 1);
        assertThat(testAccountInfo.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testAccountInfo.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingAccountInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();
        accountInfo.setId(count.incrementAndGet());

        // Create the AccountInfo
        AccountInfoDTO accountInfoDTO = accountInfoMapper.toDto(accountInfo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccountInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, accountInfoDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAccountInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();
        accountInfo.setId(count.incrementAndGet());

        // Create the AccountInfo
        AccountInfoDTO accountInfoDTO = accountInfoMapper.toDto(accountInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAccountInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();
        accountInfo.setId(count.incrementAndGet());

        // Create the AccountInfo
        AccountInfoDTO accountInfoDTO = accountInfoMapper.toDto(accountInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountInfoMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountInfoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAccountInfoWithPatch() throws Exception {
        // Initialize the database
        accountInfoRepository.saveAndFlush(accountInfo);

        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();

        // Update the accountInfo using partial update
        AccountInfo partialUpdatedAccountInfo = new AccountInfo();
        partialUpdatedAccountInfo.setId(accountInfo.getId());

        restAccountInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccountInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAccountInfo))
            )
            .andExpect(status().isOk());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
        AccountInfo testAccountInfo = accountInfoList.get(accountInfoList.size() - 1);
        assertThat(testAccountInfo.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testAccountInfo.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateAccountInfoWithPatch() throws Exception {
        // Initialize the database
        accountInfoRepository.saveAndFlush(accountInfo);

        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();

        // Update the accountInfo using partial update
        AccountInfo partialUpdatedAccountInfo = new AccountInfo();
        partialUpdatedAccountInfo.setId(accountInfo.getId());

        partialUpdatedAccountInfo.version(UPDATED_VERSION).type(UPDATED_TYPE);

        restAccountInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccountInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAccountInfo))
            )
            .andExpect(status().isOk());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
        AccountInfo testAccountInfo = accountInfoList.get(accountInfoList.size() - 1);
        assertThat(testAccountInfo.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testAccountInfo.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingAccountInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();
        accountInfo.setId(count.incrementAndGet());

        // Create the AccountInfo
        AccountInfoDTO accountInfoDTO = accountInfoMapper.toDto(accountInfo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccountInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, accountInfoDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(accountInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAccountInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();
        accountInfo.setId(count.incrementAndGet());

        // Create the AccountInfo
        AccountInfoDTO accountInfoDTO = accountInfoMapper.toDto(accountInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(accountInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAccountInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();
        accountInfo.setId(count.incrementAndGet());

        // Create the AccountInfo
        AccountInfoDTO accountInfoDTO = accountInfoMapper.toDto(accountInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountInfoMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(accountInfoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAccountInfo() throws Exception {
        // Initialize the database
        accountInfoRepository.saveAndFlush(accountInfo);

        int databaseSizeBeforeDelete = accountInfoRepository.findAll().size();

        // Delete the accountInfo
        restAccountInfoMockMvc
            .perform(delete(ENTITY_API_URL_ID, accountInfo.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
