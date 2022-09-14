package com.thegraid.lobby.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thegraid.lobby.IntegrationTest;
import com.thegraid.lobby.domain.GameInst;
import com.thegraid.lobby.domain.GameInstProps;
import com.thegraid.lobby.repository.GameInstPropsRepository;
import com.thegraid.lobby.service.dto.GameInstPropsDTO;
import com.thegraid.lobby.service.mapper.GameInstPropsMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link GameInstPropsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GameInstPropsResourceIT {

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

    private static final Instant DEFAULT_UPDATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/game-inst-props";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GameInstPropsRepository gameInstPropsRepository;

    @Autowired
    private GameInstPropsMapper gameInstPropsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGameInstPropsMockMvc;

    private GameInstProps gameInstProps;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GameInstProps createEntity(EntityManager em) {
        GameInstProps gameInstProps = new GameInstProps()
            .version(DEFAULT_VERSION)
            .seed(DEFAULT_SEED)
            .mapName(DEFAULT_MAP_NAME)
            .mapSize(DEFAULT_MAP_SIZE)
            .npcCount(DEFAULT_NPC_COUNT)
            .jsonProps(DEFAULT_JSON_PROPS)
            .updated(DEFAULT_UPDATED);
        // Add required entity
        GameInst gameInst;
        if (TestUtil.findAll(em, GameInst.class).isEmpty()) {
            gameInst = GameInstResourceIT.createEntity(em);
            em.persist(gameInst);
            em.flush();
        } else {
            gameInst = TestUtil.findAll(em, GameInst.class).get(0);
        }
        gameInstProps.setGameInst(gameInst);
        return gameInstProps;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GameInstProps createUpdatedEntity(EntityManager em) {
        GameInstProps gameInstProps = new GameInstProps()
            .version(UPDATED_VERSION)
            .seed(UPDATED_SEED)
            .mapName(UPDATED_MAP_NAME)
            .mapSize(UPDATED_MAP_SIZE)
            .npcCount(UPDATED_NPC_COUNT)
            .jsonProps(UPDATED_JSON_PROPS)
            .updated(UPDATED_UPDATED);
        // Add required entity
        GameInst gameInst;
        gameInst = GameInstResourceIT.createUpdatedEntity(em);
        em.persist(gameInst);
        em.flush();
        gameInstProps.setGameInst(gameInst);
        return gameInstProps;
    }

    @BeforeEach
    public void initTest() {
        gameInstProps = createEntity(em);
    }

    @Test
    @Transactional
    void createGameInstProps() throws Exception {
        int databaseSizeBeforeCreate = gameInstPropsRepository.findAll().size();
        // Create the GameInstProps
        GameInstPropsDTO gameInstPropsDTO = gameInstPropsMapper.toDto(gameInstProps);
        restGameInstPropsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gameInstPropsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the GameInstProps in the database
        List<GameInstProps> gameInstPropsList = gameInstPropsRepository.findAll();
        assertThat(gameInstPropsList).hasSize(databaseSizeBeforeCreate + 1);
        GameInstProps testGameInstProps = gameInstPropsList.get(gameInstPropsList.size() - 1);
        assertThat(testGameInstProps.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testGameInstProps.getSeed()).isEqualTo(DEFAULT_SEED);
        assertThat(testGameInstProps.getMapName()).isEqualTo(DEFAULT_MAP_NAME);
        assertThat(testGameInstProps.getMapSize()).isEqualTo(DEFAULT_MAP_SIZE);
        assertThat(testGameInstProps.getNpcCount()).isEqualTo(DEFAULT_NPC_COUNT);
        assertThat(testGameInstProps.getJsonProps()).isEqualTo(DEFAULT_JSON_PROPS);
        assertThat(testGameInstProps.getUpdated()).isEqualTo(DEFAULT_UPDATED);

        // Validate the id for MapsId, the ids must be same
        assertThat(testGameInstProps.getId()).isEqualTo(gameInstPropsDTO.getGameInst().getId());
    }

    @Test
    @Transactional
    void createGameInstPropsWithExistingId() throws Exception {
        // Create the GameInstProps with an existing ID
        gameInstProps.setId(1L);
        GameInstPropsDTO gameInstPropsDTO = gameInstPropsMapper.toDto(gameInstProps);

        int databaseSizeBeforeCreate = gameInstPropsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGameInstPropsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gameInstPropsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GameInstProps in the database
        List<GameInstProps> gameInstPropsList = gameInstPropsRepository.findAll();
        assertThat(gameInstPropsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateGameInstPropsMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        gameInstPropsRepository.saveAndFlush(gameInstProps);
        int databaseSizeBeforeCreate = gameInstPropsRepository.findAll().size();
        // Add a new parent entity
        GameInst gameInst = GameInstResourceIT.createUpdatedEntity(em);
        em.persist(gameInst);
        em.flush();

        // Load the gameInstProps
        GameInstProps updatedGameInstProps = gameInstPropsRepository.findById(gameInstProps.getId()).get();
        assertThat(updatedGameInstProps).isNotNull();
        // Disconnect from session so that the updates on updatedGameInstProps are not directly saved in db
        em.detach(updatedGameInstProps);

        // Update the GameInst with new association value
        updatedGameInstProps.setGameInst(gameInst);
        GameInstPropsDTO updatedGameInstPropsDTO = gameInstPropsMapper.toDto(updatedGameInstProps);
        assertThat(updatedGameInstPropsDTO).isNotNull();

        // Update the entity
        restGameInstPropsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGameInstPropsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGameInstPropsDTO))
            )
            .andExpect(status().isOk());

        // Validate the GameInstProps in the database
        List<GameInstProps> gameInstPropsList = gameInstPropsRepository.findAll();
        assertThat(gameInstPropsList).hasSize(databaseSizeBeforeCreate);
        GameInstProps testGameInstProps = gameInstPropsList.get(gameInstPropsList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testGameInstProps.getId()).isEqualTo(testGameInstProps.getGameInst().getId());
    }

    @Test
    @Transactional
    void checkUpdatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = gameInstPropsRepository.findAll().size();
        // set the field null
        gameInstProps.setUpdated(null);

        // Create the GameInstProps, which fails.
        GameInstPropsDTO gameInstPropsDTO = gameInstPropsMapper.toDto(gameInstProps);

        restGameInstPropsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gameInstPropsDTO))
            )
            .andExpect(status().isBadRequest());

        List<GameInstProps> gameInstPropsList = gameInstPropsRepository.findAll();
        assertThat(gameInstPropsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGameInstProps() throws Exception {
        // Initialize the database
        gameInstPropsRepository.saveAndFlush(gameInstProps);

        // Get all the gameInstPropsList
        restGameInstPropsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(gameInstProps.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].seed").value(hasItem(DEFAULT_SEED.intValue())))
            .andExpect(jsonPath("$.[*].mapName").value(hasItem(DEFAULT_MAP_NAME)))
            .andExpect(jsonPath("$.[*].mapSize").value(hasItem(DEFAULT_MAP_SIZE)))
            .andExpect(jsonPath("$.[*].npcCount").value(hasItem(DEFAULT_NPC_COUNT)))
            .andExpect(jsonPath("$.[*].jsonProps").value(hasItem(DEFAULT_JSON_PROPS)))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())));
    }

    @Test
    @Transactional
    void getGameInstProps() throws Exception {
        // Initialize the database
        gameInstPropsRepository.saveAndFlush(gameInstProps);

        // Get the gameInstProps
        restGameInstPropsMockMvc
            .perform(get(ENTITY_API_URL_ID, gameInstProps.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(gameInstProps.getId().intValue()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.seed").value(DEFAULT_SEED.intValue()))
            .andExpect(jsonPath("$.mapName").value(DEFAULT_MAP_NAME))
            .andExpect(jsonPath("$.mapSize").value(DEFAULT_MAP_SIZE))
            .andExpect(jsonPath("$.npcCount").value(DEFAULT_NPC_COUNT))
            .andExpect(jsonPath("$.jsonProps").value(DEFAULT_JSON_PROPS))
            .andExpect(jsonPath("$.updated").value(DEFAULT_UPDATED.toString()));
    }

    @Test
    @Transactional
    void getNonExistingGameInstProps() throws Exception {
        // Get the gameInstProps
        restGameInstPropsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGameInstProps() throws Exception {
        // Initialize the database
        gameInstPropsRepository.saveAndFlush(gameInstProps);

        int databaseSizeBeforeUpdate = gameInstPropsRepository.findAll().size();

        // Update the gameInstProps
        GameInstProps updatedGameInstProps = gameInstPropsRepository.findById(gameInstProps.getId()).get();
        // Disconnect from session so that the updates on updatedGameInstProps are not directly saved in db
        em.detach(updatedGameInstProps);
        updatedGameInstProps
            .version(UPDATED_VERSION)
            .seed(UPDATED_SEED)
            .mapName(UPDATED_MAP_NAME)
            .mapSize(UPDATED_MAP_SIZE)
            .npcCount(UPDATED_NPC_COUNT)
            .jsonProps(UPDATED_JSON_PROPS)
            .updated(UPDATED_UPDATED);
        GameInstPropsDTO gameInstPropsDTO = gameInstPropsMapper.toDto(updatedGameInstProps);

        restGameInstPropsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, gameInstPropsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gameInstPropsDTO))
            )
            .andExpect(status().isOk());

        // Validate the GameInstProps in the database
        List<GameInstProps> gameInstPropsList = gameInstPropsRepository.findAll();
        assertThat(gameInstPropsList).hasSize(databaseSizeBeforeUpdate);
        GameInstProps testGameInstProps = gameInstPropsList.get(gameInstPropsList.size() - 1);
        assertThat(testGameInstProps.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testGameInstProps.getSeed()).isEqualTo(UPDATED_SEED);
        assertThat(testGameInstProps.getMapName()).isEqualTo(UPDATED_MAP_NAME);
        assertThat(testGameInstProps.getMapSize()).isEqualTo(UPDATED_MAP_SIZE);
        assertThat(testGameInstProps.getNpcCount()).isEqualTo(UPDATED_NPC_COUNT);
        assertThat(testGameInstProps.getJsonProps()).isEqualTo(UPDATED_JSON_PROPS);
        assertThat(testGameInstProps.getUpdated()).isEqualTo(UPDATED_UPDATED);
    }

    @Test
    @Transactional
    void putNonExistingGameInstProps() throws Exception {
        int databaseSizeBeforeUpdate = gameInstPropsRepository.findAll().size();
        gameInstProps.setId(count.incrementAndGet());

        // Create the GameInstProps
        GameInstPropsDTO gameInstPropsDTO = gameInstPropsMapper.toDto(gameInstProps);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGameInstPropsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, gameInstPropsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gameInstPropsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GameInstProps in the database
        List<GameInstProps> gameInstPropsList = gameInstPropsRepository.findAll();
        assertThat(gameInstPropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGameInstProps() throws Exception {
        int databaseSizeBeforeUpdate = gameInstPropsRepository.findAll().size();
        gameInstProps.setId(count.incrementAndGet());

        // Create the GameInstProps
        GameInstPropsDTO gameInstPropsDTO = gameInstPropsMapper.toDto(gameInstProps);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGameInstPropsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gameInstPropsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GameInstProps in the database
        List<GameInstProps> gameInstPropsList = gameInstPropsRepository.findAll();
        assertThat(gameInstPropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGameInstProps() throws Exception {
        int databaseSizeBeforeUpdate = gameInstPropsRepository.findAll().size();
        gameInstProps.setId(count.incrementAndGet());

        // Create the GameInstProps
        GameInstPropsDTO gameInstPropsDTO = gameInstPropsMapper.toDto(gameInstProps);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGameInstPropsMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gameInstPropsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GameInstProps in the database
        List<GameInstProps> gameInstPropsList = gameInstPropsRepository.findAll();
        assertThat(gameInstPropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGameInstPropsWithPatch() throws Exception {
        // Initialize the database
        gameInstPropsRepository.saveAndFlush(gameInstProps);

        int databaseSizeBeforeUpdate = gameInstPropsRepository.findAll().size();

        // Update the gameInstProps using partial update
        GameInstProps partialUpdatedGameInstProps = new GameInstProps();
        partialUpdatedGameInstProps.setId(gameInstProps.getId());

        partialUpdatedGameInstProps.version(UPDATED_VERSION).seed(UPDATED_SEED).mapName(UPDATED_MAP_NAME).mapSize(UPDATED_MAP_SIZE);

        restGameInstPropsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGameInstProps.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGameInstProps))
            )
            .andExpect(status().isOk());

        // Validate the GameInstProps in the database
        List<GameInstProps> gameInstPropsList = gameInstPropsRepository.findAll();
        assertThat(gameInstPropsList).hasSize(databaseSizeBeforeUpdate);
        GameInstProps testGameInstProps = gameInstPropsList.get(gameInstPropsList.size() - 1);
        assertThat(testGameInstProps.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testGameInstProps.getSeed()).isEqualTo(UPDATED_SEED);
        assertThat(testGameInstProps.getMapName()).isEqualTo(UPDATED_MAP_NAME);
        assertThat(testGameInstProps.getMapSize()).isEqualTo(UPDATED_MAP_SIZE);
        assertThat(testGameInstProps.getNpcCount()).isEqualTo(DEFAULT_NPC_COUNT);
        assertThat(testGameInstProps.getJsonProps()).isEqualTo(DEFAULT_JSON_PROPS);
        assertThat(testGameInstProps.getUpdated()).isEqualTo(DEFAULT_UPDATED);
    }

    @Test
    @Transactional
    void fullUpdateGameInstPropsWithPatch() throws Exception {
        // Initialize the database
        gameInstPropsRepository.saveAndFlush(gameInstProps);

        int databaseSizeBeforeUpdate = gameInstPropsRepository.findAll().size();

        // Update the gameInstProps using partial update
        GameInstProps partialUpdatedGameInstProps = new GameInstProps();
        partialUpdatedGameInstProps.setId(gameInstProps.getId());

        partialUpdatedGameInstProps
            .version(UPDATED_VERSION)
            .seed(UPDATED_SEED)
            .mapName(UPDATED_MAP_NAME)
            .mapSize(UPDATED_MAP_SIZE)
            .npcCount(UPDATED_NPC_COUNT)
            .jsonProps(UPDATED_JSON_PROPS)
            .updated(UPDATED_UPDATED);

        restGameInstPropsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGameInstProps.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGameInstProps))
            )
            .andExpect(status().isOk());

        // Validate the GameInstProps in the database
        List<GameInstProps> gameInstPropsList = gameInstPropsRepository.findAll();
        assertThat(gameInstPropsList).hasSize(databaseSizeBeforeUpdate);
        GameInstProps testGameInstProps = gameInstPropsList.get(gameInstPropsList.size() - 1);
        assertThat(testGameInstProps.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testGameInstProps.getSeed()).isEqualTo(UPDATED_SEED);
        assertThat(testGameInstProps.getMapName()).isEqualTo(UPDATED_MAP_NAME);
        assertThat(testGameInstProps.getMapSize()).isEqualTo(UPDATED_MAP_SIZE);
        assertThat(testGameInstProps.getNpcCount()).isEqualTo(UPDATED_NPC_COUNT);
        assertThat(testGameInstProps.getJsonProps()).isEqualTo(UPDATED_JSON_PROPS);
        assertThat(testGameInstProps.getUpdated()).isEqualTo(UPDATED_UPDATED);
    }

    @Test
    @Transactional
    void patchNonExistingGameInstProps() throws Exception {
        int databaseSizeBeforeUpdate = gameInstPropsRepository.findAll().size();
        gameInstProps.setId(count.incrementAndGet());

        // Create the GameInstProps
        GameInstPropsDTO gameInstPropsDTO = gameInstPropsMapper.toDto(gameInstProps);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGameInstPropsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, gameInstPropsDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(gameInstPropsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GameInstProps in the database
        List<GameInstProps> gameInstPropsList = gameInstPropsRepository.findAll();
        assertThat(gameInstPropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGameInstProps() throws Exception {
        int databaseSizeBeforeUpdate = gameInstPropsRepository.findAll().size();
        gameInstProps.setId(count.incrementAndGet());

        // Create the GameInstProps
        GameInstPropsDTO gameInstPropsDTO = gameInstPropsMapper.toDto(gameInstProps);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGameInstPropsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(gameInstPropsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GameInstProps in the database
        List<GameInstProps> gameInstPropsList = gameInstPropsRepository.findAll();
        assertThat(gameInstPropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGameInstProps() throws Exception {
        int databaseSizeBeforeUpdate = gameInstPropsRepository.findAll().size();
        gameInstProps.setId(count.incrementAndGet());

        // Create the GameInstProps
        GameInstPropsDTO gameInstPropsDTO = gameInstPropsMapper.toDto(gameInstProps);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGameInstPropsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(gameInstPropsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GameInstProps in the database
        List<GameInstProps> gameInstPropsList = gameInstPropsRepository.findAll();
        assertThat(gameInstPropsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGameInstProps() throws Exception {
        // Initialize the database
        gameInstPropsRepository.saveAndFlush(gameInstProps);

        int databaseSizeBeforeDelete = gameInstPropsRepository.findAll().size();

        // Delete the gameInstProps
        restGameInstPropsMockMvc
            .perform(delete(ENTITY_API_URL_ID, gameInstProps.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GameInstProps> gameInstPropsList = gameInstPropsRepository.findAll();
        assertThat(gameInstPropsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
