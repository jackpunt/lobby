package com.thegraid.lobby.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thegraid.lobby.IntegrationTest;
import com.thegraid.lobby.domain.GamePlayer;
import com.thegraid.lobby.repository.GamePlayerRepository;
import com.thegraid.lobby.service.dto.GamePlayerDTO;
import com.thegraid.lobby.service.mapper.GamePlayerMapper;
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
 * Integration tests for the {@link GamePlayerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GamePlayerResourceIT {

    private static final Integer DEFAULT_VERSION = 1;
    private static final Integer UPDATED_VERSION = 2;

    private static final String DEFAULT_ROLE = "AAAA";
    private static final String UPDATED_ROLE = "BBBB";

    private static final Integer DEFAULT_READY = 1;
    private static final Integer UPDATED_READY = 2;

    private static final String ENTITY_API_URL = "/api/game-players";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private GamePlayerMapper gamePlayerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGamePlayerMockMvc;

    private GamePlayer gamePlayer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GamePlayer createEntity(EntityManager em) {
        GamePlayer gamePlayer = new GamePlayer().version(DEFAULT_VERSION).role(DEFAULT_ROLE).ready(DEFAULT_READY);
        return gamePlayer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GamePlayer createUpdatedEntity(EntityManager em) {
        GamePlayer gamePlayer = new GamePlayer().version(UPDATED_VERSION).role(UPDATED_ROLE).ready(UPDATED_READY);
        return gamePlayer;
    }

    @BeforeEach
    public void initTest() {
        gamePlayer = createEntity(em);
    }

    @Test
    @Transactional
    void createGamePlayer() throws Exception {
        int databaseSizeBeforeCreate = gamePlayerRepository.findAll().size();
        // Create the GamePlayer
        GamePlayerDTO gamePlayerDTO = gamePlayerMapper.toDto(gamePlayer);
        restGamePlayerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gamePlayerDTO))
            )
            .andExpect(status().isCreated());

        // Validate the GamePlayer in the database
        List<GamePlayer> gamePlayerList = gamePlayerRepository.findAll();
        assertThat(gamePlayerList).hasSize(databaseSizeBeforeCreate + 1);
        GamePlayer testGamePlayer = gamePlayerList.get(gamePlayerList.size() - 1);
        assertThat(testGamePlayer.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testGamePlayer.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(testGamePlayer.getReady()).isEqualTo(DEFAULT_READY);
    }

    @Test
    @Transactional
    void createGamePlayerWithExistingId() throws Exception {
        // Create the GamePlayer with an existing ID
        gamePlayer.setId(1L);
        GamePlayerDTO gamePlayerDTO = gamePlayerMapper.toDto(gamePlayer);

        int databaseSizeBeforeCreate = gamePlayerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGamePlayerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gamePlayerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GamePlayer in the database
        List<GamePlayer> gamePlayerList = gamePlayerRepository.findAll();
        assertThat(gamePlayerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRoleIsRequired() throws Exception {
        int databaseSizeBeforeTest = gamePlayerRepository.findAll().size();
        // set the field null
        gamePlayer.setRole(null);

        // Create the GamePlayer, which fails.
        GamePlayerDTO gamePlayerDTO = gamePlayerMapper.toDto(gamePlayer);

        restGamePlayerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gamePlayerDTO))
            )
            .andExpect(status().isBadRequest());

        List<GamePlayer> gamePlayerList = gamePlayerRepository.findAll();
        assertThat(gamePlayerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReadyIsRequired() throws Exception {
        int databaseSizeBeforeTest = gamePlayerRepository.findAll().size();
        // set the field null
        gamePlayer.setReady(null);

        // Create the GamePlayer, which fails.
        GamePlayerDTO gamePlayerDTO = gamePlayerMapper.toDto(gamePlayer);

        restGamePlayerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gamePlayerDTO))
            )
            .andExpect(status().isBadRequest());

        List<GamePlayer> gamePlayerList = gamePlayerRepository.findAll();
        assertThat(gamePlayerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGamePlayers() throws Exception {
        // Initialize the database
        gamePlayerRepository.saveAndFlush(gamePlayer);

        // Get all the gamePlayerList
        restGamePlayerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(gamePlayer.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE)))
            .andExpect(jsonPath("$.[*].ready").value(hasItem(DEFAULT_READY)));
    }

    @Test
    @Transactional
    void getGamePlayer() throws Exception {
        // Initialize the database
        gamePlayerRepository.saveAndFlush(gamePlayer);

        // Get the gamePlayer
        restGamePlayerMockMvc
            .perform(get(ENTITY_API_URL_ID, gamePlayer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(gamePlayer.getId().intValue()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE))
            .andExpect(jsonPath("$.ready").value(DEFAULT_READY));
    }

    @Test
    @Transactional
    void getNonExistingGamePlayer() throws Exception {
        // Get the gamePlayer
        restGamePlayerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGamePlayer() throws Exception {
        // Initialize the database
        gamePlayerRepository.saveAndFlush(gamePlayer);

        int databaseSizeBeforeUpdate = gamePlayerRepository.findAll().size();

        // Update the gamePlayer
        GamePlayer updatedGamePlayer = gamePlayerRepository.findById(gamePlayer.getId()).get();
        // Disconnect from session so that the updates on updatedGamePlayer are not directly saved in db
        em.detach(updatedGamePlayer);
        updatedGamePlayer.version(UPDATED_VERSION).role(UPDATED_ROLE).ready(UPDATED_READY);
        GamePlayerDTO gamePlayerDTO = gamePlayerMapper.toDto(updatedGamePlayer);

        restGamePlayerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, gamePlayerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gamePlayerDTO))
            )
            .andExpect(status().isOk());

        // Validate the GamePlayer in the database
        List<GamePlayer> gamePlayerList = gamePlayerRepository.findAll();
        assertThat(gamePlayerList).hasSize(databaseSizeBeforeUpdate);
        GamePlayer testGamePlayer = gamePlayerList.get(gamePlayerList.size() - 1);
        assertThat(testGamePlayer.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testGamePlayer.getRole()).isEqualTo(UPDATED_ROLE);
        assertThat(testGamePlayer.getReady()).isEqualTo(UPDATED_READY);
    }

    @Test
    @Transactional
    void putNonExistingGamePlayer() throws Exception {
        int databaseSizeBeforeUpdate = gamePlayerRepository.findAll().size();
        gamePlayer.setId(count.incrementAndGet());

        // Create the GamePlayer
        GamePlayerDTO gamePlayerDTO = gamePlayerMapper.toDto(gamePlayer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGamePlayerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, gamePlayerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gamePlayerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GamePlayer in the database
        List<GamePlayer> gamePlayerList = gamePlayerRepository.findAll();
        assertThat(gamePlayerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGamePlayer() throws Exception {
        int databaseSizeBeforeUpdate = gamePlayerRepository.findAll().size();
        gamePlayer.setId(count.incrementAndGet());

        // Create the GamePlayer
        GamePlayerDTO gamePlayerDTO = gamePlayerMapper.toDto(gamePlayer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGamePlayerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gamePlayerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GamePlayer in the database
        List<GamePlayer> gamePlayerList = gamePlayerRepository.findAll();
        assertThat(gamePlayerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGamePlayer() throws Exception {
        int databaseSizeBeforeUpdate = gamePlayerRepository.findAll().size();
        gamePlayer.setId(count.incrementAndGet());

        // Create the GamePlayer
        GamePlayerDTO gamePlayerDTO = gamePlayerMapper.toDto(gamePlayer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGamePlayerMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gamePlayerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GamePlayer in the database
        List<GamePlayer> gamePlayerList = gamePlayerRepository.findAll();
        assertThat(gamePlayerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGamePlayerWithPatch() throws Exception {
        // Initialize the database
        gamePlayerRepository.saveAndFlush(gamePlayer);

        int databaseSizeBeforeUpdate = gamePlayerRepository.findAll().size();

        // Update the gamePlayer using partial update
        GamePlayer partialUpdatedGamePlayer = new GamePlayer();
        partialUpdatedGamePlayer.setId(gamePlayer.getId());

        partialUpdatedGamePlayer.version(UPDATED_VERSION).ready(UPDATED_READY);

        restGamePlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGamePlayer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGamePlayer))
            )
            .andExpect(status().isOk());

        // Validate the GamePlayer in the database
        List<GamePlayer> gamePlayerList = gamePlayerRepository.findAll();
        assertThat(gamePlayerList).hasSize(databaseSizeBeforeUpdate);
        GamePlayer testGamePlayer = gamePlayerList.get(gamePlayerList.size() - 1);
        assertThat(testGamePlayer.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testGamePlayer.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(testGamePlayer.getReady()).isEqualTo(UPDATED_READY);
    }

    @Test
    @Transactional
    void fullUpdateGamePlayerWithPatch() throws Exception {
        // Initialize the database
        gamePlayerRepository.saveAndFlush(gamePlayer);

        int databaseSizeBeforeUpdate = gamePlayerRepository.findAll().size();

        // Update the gamePlayer using partial update
        GamePlayer partialUpdatedGamePlayer = new GamePlayer();
        partialUpdatedGamePlayer.setId(gamePlayer.getId());

        partialUpdatedGamePlayer.version(UPDATED_VERSION).role(UPDATED_ROLE).ready(UPDATED_READY);

        restGamePlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGamePlayer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGamePlayer))
            )
            .andExpect(status().isOk());

        // Validate the GamePlayer in the database
        List<GamePlayer> gamePlayerList = gamePlayerRepository.findAll();
        assertThat(gamePlayerList).hasSize(databaseSizeBeforeUpdate);
        GamePlayer testGamePlayer = gamePlayerList.get(gamePlayerList.size() - 1);
        assertThat(testGamePlayer.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testGamePlayer.getRole()).isEqualTo(UPDATED_ROLE);
        assertThat(testGamePlayer.getReady()).isEqualTo(UPDATED_READY);
    }

    @Test
    @Transactional
    void patchNonExistingGamePlayer() throws Exception {
        int databaseSizeBeforeUpdate = gamePlayerRepository.findAll().size();
        gamePlayer.setId(count.incrementAndGet());

        // Create the GamePlayer
        GamePlayerDTO gamePlayerDTO = gamePlayerMapper.toDto(gamePlayer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGamePlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, gamePlayerDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(gamePlayerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GamePlayer in the database
        List<GamePlayer> gamePlayerList = gamePlayerRepository.findAll();
        assertThat(gamePlayerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGamePlayer() throws Exception {
        int databaseSizeBeforeUpdate = gamePlayerRepository.findAll().size();
        gamePlayer.setId(count.incrementAndGet());

        // Create the GamePlayer
        GamePlayerDTO gamePlayerDTO = gamePlayerMapper.toDto(gamePlayer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGamePlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(gamePlayerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GamePlayer in the database
        List<GamePlayer> gamePlayerList = gamePlayerRepository.findAll();
        assertThat(gamePlayerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGamePlayer() throws Exception {
        int databaseSizeBeforeUpdate = gamePlayerRepository.findAll().size();
        gamePlayer.setId(count.incrementAndGet());

        // Create the GamePlayer
        GamePlayerDTO gamePlayerDTO = gamePlayerMapper.toDto(gamePlayer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGamePlayerMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(gamePlayerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GamePlayer in the database
        List<GamePlayer> gamePlayerList = gamePlayerRepository.findAll();
        assertThat(gamePlayerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGamePlayer() throws Exception {
        // Initialize the database
        gamePlayerRepository.saveAndFlush(gamePlayer);

        int databaseSizeBeforeDelete = gamePlayerRepository.findAll().size();

        // Delete the gamePlayer
        restGamePlayerMockMvc
            .perform(delete(ENTITY_API_URL_ID, gamePlayer.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GamePlayer> gamePlayerList = gamePlayerRepository.findAll();
        assertThat(gamePlayerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
