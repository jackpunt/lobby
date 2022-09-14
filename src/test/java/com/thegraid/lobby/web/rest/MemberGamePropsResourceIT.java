package com.thegraid.lobby.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thegraid.lobby.IntegrationTest;
import com.thegraid.lobby.domain.MemberGameProps;
import com.thegraid.lobby.repository.MemberGamePropsRepository;
import com.thegraid.lobby.service.dto.MemberGamePropsDTO;
import com.thegraid.lobby.service.mapper.MemberGamePropsMapper;
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
 * Integration tests for the {@link MemberGamePropsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MemberGamePropsResourceIT {

    private static final Integer DEFAULT_VERSION = 1;
    private static final Integer UPDATED_VERSION = 2;

    private static final Long DEFAULT_SEED = 1L;
    private static final Long UPDATED_SEED = 2L;

    private static final String DEFAULT_MAP_NAME = "AAAAAAAAAA";
    private static final String UPDATED_MAP_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_MAP_SIZE = 1;
    private static final Integer UPDATED_MAP_SIZE = 2;

    private static final Integer DEFAULT_NPC_COUNT = 1;
    private static final Integer UPDATED_NPC_COUNT = 2;

    private static final String DEFAULT_JSON_PROPS = "AAAAAAAAAA";
    private static final String UPDATED_JSON_PROPS = "BBBBBBBBBB";

    private static final String DEFAULT_CONFIG_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CONFIG_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/member-game-props";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MemberGamePropsRepository memberGamePropsRepository;

    @Autowired
    private MemberGamePropsMapper memberGamePropsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMemberGamePropsMockMvc;

    private MemberGameProps memberGameProps;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MemberGameProps createEntity(EntityManager em) {
        MemberGameProps memberGameProps = new MemberGameProps()
            .version(DEFAULT_VERSION)
            .seed(DEFAULT_SEED)
            .mapName(DEFAULT_MAP_NAME)
            .mapSize(DEFAULT_MAP_SIZE)
            .npcCount(DEFAULT_NPC_COUNT)
            .jsonProps(DEFAULT_JSON_PROPS)
            .configName(DEFAULT_CONFIG_NAME);
        return memberGameProps;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MemberGameProps createUpdatedEntity(EntityManager em) {
        MemberGameProps memberGameProps = new MemberGameProps()
            .version(UPDATED_VERSION)
            .seed(UPDATED_SEED)
            .mapName(UPDATED_MAP_NAME)
            .mapSize(UPDATED_MAP_SIZE)
            .npcCount(UPDATED_NPC_COUNT)
            .jsonProps(UPDATED_JSON_PROPS)
            .configName(UPDATED_CONFIG_NAME);
        return memberGameProps;
    }

    @BeforeEach
    public void initTest() {
        memberGameProps = createEntity(em);
    }

    @Test
    @Transactional
    void createMemberGameProps() throws Exception {
        int databaseSizeBeforeCreate = memberGamePropsRepository.findAll().size();
        // Create the MemberGameProps
        MemberGamePropsDTO memberGamePropsDTO = memberGamePropsMapper.toDto(memberGameProps);
        restMemberGamePropsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(memberGamePropsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the MemberGameProps in the database
        List<MemberGameProps> memberGamePropsList = memberGamePropsRepository.findAll();
        assertThat(memberGamePropsList).hasSize(databaseSizeBeforeCreate + 1);
        MemberGameProps testMemberGameProps = memberGamePropsList.get(memberGamePropsList.size() - 1);
        assertThat(testMemberGameProps.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testMemberGameProps.getSeed()).isEqualTo(DEFAULT_SEED);
        assertThat(testMemberGameProps.getMapName()).isEqualTo(DEFAULT_MAP_NAME);
        assertThat(testMemberGameProps.getMapSize()).isEqualTo(DEFAULT_MAP_SIZE);
        assertThat(testMemberGameProps.getNpcCount()).isEqualTo(DEFAULT_NPC_COUNT);
        assertThat(testMemberGameProps.getJsonProps()).isEqualTo(DEFAULT_JSON_PROPS);
        assertThat(testMemberGameProps.getConfigName()).isEqualTo(DEFAULT_CONFIG_NAME);
    }

    @Test
    @Transactional
    void createMemberGamePropsWithExistingId() throws Exception {
        // Create the MemberGameProps with an existing ID
        memberGameProps.setId(1L);
        MemberGamePropsDTO memberGamePropsDTO = memberGamePropsMapper.toDto(memberGameProps);

        int databaseSizeBeforeCreate = memberGamePropsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMemberGamePropsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(memberGamePropsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MemberGameProps in the database
        List<MemberGameProps> memberGamePropsList = memberGamePropsRepository.findAll();
        assertThat(memberGamePropsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMemberGameProps() throws Exception {
        // Initialize the database
        memberGamePropsRepository.saveAndFlush(memberGameProps);

        // Get all the memberGamePropsList
        restMemberGamePropsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(memberGameProps.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].seed").value(hasItem(DEFAULT_SEED.intValue())))
            .andExpect(jsonPath("$.[*].mapName").value(hasItem(DEFAULT_MAP_NAME)))
            .andExpect(jsonPath("$.[*].mapSize").value(hasItem(DEFAULT_MAP_SIZE)))
            .andExpect(jsonPath("$.[*].npcCount").value(hasItem(DEFAULT_NPC_COUNT)))
            .andExpect(jsonPath("$.[*].jsonProps").value(hasItem(DEFAULT_JSON_PROPS)))
            .andExpect(jsonPath("$.[*].configName").value(hasItem(DEFAULT_CONFIG_NAME)));
    }

    @Test
    @Transactional
    void getMemberGameProps() throws Exception {
        // Initialize the database
        memberGamePropsRepository.saveAndFlush(memberGameProps);

        // Get the memberGameProps
        restMemberGamePropsMockMvc
            .perform(get(ENTITY_API_URL_ID, memberGameProps.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(memberGameProps.getId().intValue()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.seed").value(DEFAULT_SEED.intValue()))
            .andExpect(jsonPath("$.mapName").value(DEFAULT_MAP_NAME))
            .andExpect(jsonPath("$.mapSize").value(DEFAULT_MAP_SIZE))
            .andExpect(jsonPath("$.npcCount").value(DEFAULT_NPC_COUNT))
            .andExpect(jsonPath("$.jsonProps").value(DEFAULT_JSON_PROPS))
            .andExpect(jsonPath("$.configName").value(DEFAULT_CONFIG_NAME));
    }

    @Test
    @Transactional
    void getNonExistingMemberGameProps() throws Exception {
        // Get the memberGameProps
        restMemberGamePropsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewMemberGameProps() throws Exception {
        // Initialize the database
        memberGamePropsRepository.saveAndFlush(memberGameProps);

        int databaseSizeBeforeUpdate = memberGamePropsRepository.findAll().size();

        // Update the memberGameProps
        MemberGameProps updatedMemberGameProps = memberGamePropsRepository.findById(memberGameProps.getId()).get();
        // Disconnect from session so that the updates on updatedMemberGameProps are not directly saved in db
        em.detach(updatedMemberGameProps);
        updatedMemberGameProps
            .version(UPDATED_VERSION)
            .seed(UPDATED_SEED)
            .mapName(UPDATED_MAP_NAME)
            .mapSize(UPDATED_MAP_SIZE)
            .npcCount(UPDATED_NPC_COUNT)
            .jsonProps(UPDATED_JSON_PROPS)
            .configName(UPDATED_CONFIG_NAME);
        MemberGamePropsDTO memberGamePropsDTO = memberGamePropsMapper.toDto(updatedMemberGameProps);

        restMemberGamePropsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, memberGamePropsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(memberGamePropsDTO))
            )
            .andExpect(status().isOk());

        // Validate the MemberGameProps in the database
        List<MemberGameProps> memberGamePropsList = memberGamePropsRepository.findAll();
        assertThat(memberGamePropsList).hasSize(databaseSizeBeforeUpdate);
        MemberGameProps testMemberGameProps = memberGamePropsList.get(memberGamePropsList.size() - 1);
        assertThat(testMemberGameProps.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testMemberGameProps.getSeed()).isEqualTo(UPDATED_SEED);
        assertThat(testMemberGameProps.getMapName()).isEqualTo(UPDATED_MAP_NAME);
        assertThat(testMemberGameProps.getMapSize()).isEqualTo(UPDATED_MAP_SIZE);
        assertThat(testMemberGameProps.getNpcCount()).isEqualTo(UPDATED_NPC_COUNT);
        assertThat(testMemberGameProps.getJsonProps()).isEqualTo(UPDATED_JSON_PROPS);
        assertThat(testMemberGameProps.getConfigName()).isEqualTo(UPDATED_CONFIG_NAME);
    }

    @Test
    @Transactional
    void putNonExistingMemberGameProps() throws Exception {
        int databaseSizeBeforeUpdate = memberGamePropsRepository.findAll().size();
        memberGameProps.setId(count.incrementAndGet());

        // Create the MemberGameProps
        MemberGamePropsDTO memberGamePropsDTO = memberGamePropsMapper.toDto(memberGameProps);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMemberGamePropsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, memberGamePropsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(memberGamePropsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MemberGameProps in the database
        List<MemberGameProps> memberGamePropsList = memberGamePropsRepository.findAll();
        assertThat(memberGamePropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMemberGameProps() throws Exception {
        int databaseSizeBeforeUpdate = memberGamePropsRepository.findAll().size();
        memberGameProps.setId(count.incrementAndGet());

        // Create the MemberGameProps
        MemberGamePropsDTO memberGamePropsDTO = memberGamePropsMapper.toDto(memberGameProps);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMemberGamePropsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(memberGamePropsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MemberGameProps in the database
        List<MemberGameProps> memberGamePropsList = memberGamePropsRepository.findAll();
        assertThat(memberGamePropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMemberGameProps() throws Exception {
        int databaseSizeBeforeUpdate = memberGamePropsRepository.findAll().size();
        memberGameProps.setId(count.incrementAndGet());

        // Create the MemberGameProps
        MemberGamePropsDTO memberGamePropsDTO = memberGamePropsMapper.toDto(memberGameProps);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMemberGamePropsMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(memberGamePropsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MemberGameProps in the database
        List<MemberGameProps> memberGamePropsList = memberGamePropsRepository.findAll();
        assertThat(memberGamePropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMemberGamePropsWithPatch() throws Exception {
        // Initialize the database
        memberGamePropsRepository.saveAndFlush(memberGameProps);

        int databaseSizeBeforeUpdate = memberGamePropsRepository.findAll().size();

        // Update the memberGameProps using partial update
        MemberGameProps partialUpdatedMemberGameProps = new MemberGameProps();
        partialUpdatedMemberGameProps.setId(memberGameProps.getId());

        partialUpdatedMemberGameProps.mapName(UPDATED_MAP_NAME).jsonProps(UPDATED_JSON_PROPS);

        restMemberGamePropsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMemberGameProps.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMemberGameProps))
            )
            .andExpect(status().isOk());

        // Validate the MemberGameProps in the database
        List<MemberGameProps> memberGamePropsList = memberGamePropsRepository.findAll();
        assertThat(memberGamePropsList).hasSize(databaseSizeBeforeUpdate);
        MemberGameProps testMemberGameProps = memberGamePropsList.get(memberGamePropsList.size() - 1);
        assertThat(testMemberGameProps.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testMemberGameProps.getSeed()).isEqualTo(DEFAULT_SEED);
        assertThat(testMemberGameProps.getMapName()).isEqualTo(UPDATED_MAP_NAME);
        assertThat(testMemberGameProps.getMapSize()).isEqualTo(DEFAULT_MAP_SIZE);
        assertThat(testMemberGameProps.getNpcCount()).isEqualTo(DEFAULT_NPC_COUNT);
        assertThat(testMemberGameProps.getJsonProps()).isEqualTo(UPDATED_JSON_PROPS);
        assertThat(testMemberGameProps.getConfigName()).isEqualTo(DEFAULT_CONFIG_NAME);
    }

    @Test
    @Transactional
    void fullUpdateMemberGamePropsWithPatch() throws Exception {
        // Initialize the database
        memberGamePropsRepository.saveAndFlush(memberGameProps);

        int databaseSizeBeforeUpdate = memberGamePropsRepository.findAll().size();

        // Update the memberGameProps using partial update
        MemberGameProps partialUpdatedMemberGameProps = new MemberGameProps();
        partialUpdatedMemberGameProps.setId(memberGameProps.getId());

        partialUpdatedMemberGameProps
            .version(UPDATED_VERSION)
            .seed(UPDATED_SEED)
            .mapName(UPDATED_MAP_NAME)
            .mapSize(UPDATED_MAP_SIZE)
            .npcCount(UPDATED_NPC_COUNT)
            .jsonProps(UPDATED_JSON_PROPS)
            .configName(UPDATED_CONFIG_NAME);

        restMemberGamePropsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMemberGameProps.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMemberGameProps))
            )
            .andExpect(status().isOk());

        // Validate the MemberGameProps in the database
        List<MemberGameProps> memberGamePropsList = memberGamePropsRepository.findAll();
        assertThat(memberGamePropsList).hasSize(databaseSizeBeforeUpdate);
        MemberGameProps testMemberGameProps = memberGamePropsList.get(memberGamePropsList.size() - 1);
        assertThat(testMemberGameProps.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testMemberGameProps.getSeed()).isEqualTo(UPDATED_SEED);
        assertThat(testMemberGameProps.getMapName()).isEqualTo(UPDATED_MAP_NAME);
        assertThat(testMemberGameProps.getMapSize()).isEqualTo(UPDATED_MAP_SIZE);
        assertThat(testMemberGameProps.getNpcCount()).isEqualTo(UPDATED_NPC_COUNT);
        assertThat(testMemberGameProps.getJsonProps()).isEqualTo(UPDATED_JSON_PROPS);
        assertThat(testMemberGameProps.getConfigName()).isEqualTo(UPDATED_CONFIG_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingMemberGameProps() throws Exception {
        int databaseSizeBeforeUpdate = memberGamePropsRepository.findAll().size();
        memberGameProps.setId(count.incrementAndGet());

        // Create the MemberGameProps
        MemberGamePropsDTO memberGamePropsDTO = memberGamePropsMapper.toDto(memberGameProps);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMemberGamePropsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, memberGamePropsDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(memberGamePropsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MemberGameProps in the database
        List<MemberGameProps> memberGamePropsList = memberGamePropsRepository.findAll();
        assertThat(memberGamePropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMemberGameProps() throws Exception {
        int databaseSizeBeforeUpdate = memberGamePropsRepository.findAll().size();
        memberGameProps.setId(count.incrementAndGet());

        // Create the MemberGameProps
        MemberGamePropsDTO memberGamePropsDTO = memberGamePropsMapper.toDto(memberGameProps);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMemberGamePropsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(memberGamePropsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MemberGameProps in the database
        List<MemberGameProps> memberGamePropsList = memberGamePropsRepository.findAll();
        assertThat(memberGamePropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMemberGameProps() throws Exception {
        int databaseSizeBeforeUpdate = memberGamePropsRepository.findAll().size();
        memberGameProps.setId(count.incrementAndGet());

        // Create the MemberGameProps
        MemberGamePropsDTO memberGamePropsDTO = memberGamePropsMapper.toDto(memberGameProps);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMemberGamePropsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(memberGamePropsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MemberGameProps in the database
        List<MemberGameProps> memberGamePropsList = memberGamePropsRepository.findAll();
        assertThat(memberGamePropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMemberGameProps() throws Exception {
        // Initialize the database
        memberGamePropsRepository.saveAndFlush(memberGameProps);

        int databaseSizeBeforeDelete = memberGamePropsRepository.findAll().size();

        // Delete the memberGameProps
        restMemberGamePropsMockMvc
            .perform(delete(ENTITY_API_URL_ID, memberGameProps.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<MemberGameProps> memberGamePropsList = memberGamePropsRepository.findAll();
        assertThat(memberGamePropsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
