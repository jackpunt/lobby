package com.thegraid.lobby.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thegraid.lobby.IntegrationTest;
import com.thegraid.lobby.domain.Player;
import com.thegraid.lobby.repository.PlayerRepository;
import com.thegraid.lobby.service.dto.PlayerDTO;
import com.thegraid.lobby.service.mapper.PlayerMapper;
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
 * Integration tests for the {@link PlayerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PlayerResourceIT {

    private static final Integer DEFAULT_VERSION = 1;
    private static final Integer UPDATED_VERSION = 2;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_RANK = 1;
    private static final Integer UPDATED_RANK = 2;

    private static final Integer DEFAULT_SCORE = 1;
    private static final Integer UPDATED_SCORE = 2;

    private static final Instant DEFAULT_SCORE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SCORE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_RANK_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RANK_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DISPLAY_CLIENT = "AAAAAAAAAA";
    private static final String UPDATED_DISPLAY_CLIENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/players";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlayerMockMvc;

    private Player player;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Player createEntity(EntityManager em) {
        Player player = new Player()
            .version(DEFAULT_VERSION)
            .name(DEFAULT_NAME)
            .rank(DEFAULT_RANK)
            .score(DEFAULT_SCORE)
            .scoreTime(DEFAULT_SCORE_TIME)
            .rankTime(DEFAULT_RANK_TIME)
            .displayClient(DEFAULT_DISPLAY_CLIENT);
        return player;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Player createUpdatedEntity(EntityManager em) {
        Player player = new Player()
            .version(UPDATED_VERSION)
            .name(UPDATED_NAME)
            .rank(UPDATED_RANK)
            .score(UPDATED_SCORE)
            .scoreTime(UPDATED_SCORE_TIME)
            .rankTime(UPDATED_RANK_TIME)
            .displayClient(UPDATED_DISPLAY_CLIENT);
        return player;
    }

    @BeforeEach
    public void initTest() {
        player = createEntity(em);
    }

    @Test
    @Transactional
    void createPlayer() throws Exception {
        int databaseSizeBeforeCreate = playerRepository.findAll().size();
        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);
        restPlayerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(playerDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeCreate + 1);
        Player testPlayer = playerList.get(playerList.size() - 1);
        assertThat(testPlayer.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testPlayer.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPlayer.getRank()).isEqualTo(DEFAULT_RANK);
        assertThat(testPlayer.getScore()).isEqualTo(DEFAULT_SCORE);
        assertThat(testPlayer.getScoreTime()).isEqualTo(DEFAULT_SCORE_TIME);
        assertThat(testPlayer.getRankTime()).isEqualTo(DEFAULT_RANK_TIME);
        assertThat(testPlayer.getDisplayClient()).isEqualTo(DEFAULT_DISPLAY_CLIENT);
    }

    @Test
    @Transactional
    void createPlayerWithExistingId() throws Exception {
        // Create the Player with an existing ID
        player.setId(1L);
        PlayerDTO playerDTO = playerMapper.toDto(player);

        int databaseSizeBeforeCreate = playerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlayerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(playerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPlayers() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList
        restPlayerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(player.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK)))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)))
            .andExpect(jsonPath("$.[*].scoreTime").value(hasItem(DEFAULT_SCORE_TIME.toString())))
            .andExpect(jsonPath("$.[*].rankTime").value(hasItem(DEFAULT_RANK_TIME.toString())))
            .andExpect(jsonPath("$.[*].displayClient").value(hasItem(DEFAULT_DISPLAY_CLIENT)));
    }

    @Test
    @Transactional
    void getPlayer() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get the player
        restPlayerMockMvc
            .perform(get(ENTITY_API_URL_ID, player.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(player.getId().intValue()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.rank").value(DEFAULT_RANK))
            .andExpect(jsonPath("$.score").value(DEFAULT_SCORE))
            .andExpect(jsonPath("$.scoreTime").value(DEFAULT_SCORE_TIME.toString()))
            .andExpect(jsonPath("$.rankTime").value(DEFAULT_RANK_TIME.toString()))
            .andExpect(jsonPath("$.displayClient").value(DEFAULT_DISPLAY_CLIENT));
    }

    @Test
    @Transactional
    void getNonExistingPlayer() throws Exception {
        // Get the player
        restPlayerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPlayer() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        int databaseSizeBeforeUpdate = playerRepository.findAll().size();

        // Update the player
        Player updatedPlayer = playerRepository.findById(player.getId()).get();
        // Disconnect from session so that the updates on updatedPlayer are not directly saved in db
        em.detach(updatedPlayer);
        updatedPlayer
            .version(UPDATED_VERSION)
            .name(UPDATED_NAME)
            .rank(UPDATED_RANK)
            .score(UPDATED_SCORE)
            .scoreTime(UPDATED_SCORE_TIME)
            .rankTime(UPDATED_RANK_TIME)
            .displayClient(UPDATED_DISPLAY_CLIENT);
        PlayerDTO playerDTO = playerMapper.toDto(updatedPlayer);

        restPlayerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, playerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(playerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);
        Player testPlayer = playerList.get(playerList.size() - 1);
        assertThat(testPlayer.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testPlayer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPlayer.getRank()).isEqualTo(UPDATED_RANK);
        assertThat(testPlayer.getScore()).isEqualTo(UPDATED_SCORE);
        assertThat(testPlayer.getScoreTime()).isEqualTo(UPDATED_SCORE_TIME);
        assertThat(testPlayer.getRankTime()).isEqualTo(UPDATED_RANK_TIME);
        assertThat(testPlayer.getDisplayClient()).isEqualTo(UPDATED_DISPLAY_CLIENT);
    }

    @Test
    @Transactional
    void putNonExistingPlayer() throws Exception {
        int databaseSizeBeforeUpdate = playerRepository.findAll().size();
        player.setId(count.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, playerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(playerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlayer() throws Exception {
        int databaseSizeBeforeUpdate = playerRepository.findAll().size();
        player.setId(count.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(playerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlayer() throws Exception {
        int databaseSizeBeforeUpdate = playerRepository.findAll().size();
        player.setId(count.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(playerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePlayerWithPatch() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        int databaseSizeBeforeUpdate = playerRepository.findAll().size();

        // Update the player using partial update
        Player partialUpdatedPlayer = new Player();
        partialUpdatedPlayer.setId(player.getId());

        partialUpdatedPlayer
            .version(UPDATED_VERSION)
            .name(UPDATED_NAME)
            .rank(UPDATED_RANK)
            .scoreTime(UPDATED_SCORE_TIME)
            .rankTime(UPDATED_RANK_TIME)
            .displayClient(UPDATED_DISPLAY_CLIENT);

        restPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlayer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlayer))
            )
            .andExpect(status().isOk());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);
        Player testPlayer = playerList.get(playerList.size() - 1);
        assertThat(testPlayer.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testPlayer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPlayer.getRank()).isEqualTo(UPDATED_RANK);
        assertThat(testPlayer.getScore()).isEqualTo(DEFAULT_SCORE);
        assertThat(testPlayer.getScoreTime()).isEqualTo(UPDATED_SCORE_TIME);
        assertThat(testPlayer.getRankTime()).isEqualTo(UPDATED_RANK_TIME);
        assertThat(testPlayer.getDisplayClient()).isEqualTo(UPDATED_DISPLAY_CLIENT);
    }

    @Test
    @Transactional
    void fullUpdatePlayerWithPatch() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        int databaseSizeBeforeUpdate = playerRepository.findAll().size();

        // Update the player using partial update
        Player partialUpdatedPlayer = new Player();
        partialUpdatedPlayer.setId(player.getId());

        partialUpdatedPlayer
            .version(UPDATED_VERSION)
            .name(UPDATED_NAME)
            .rank(UPDATED_RANK)
            .score(UPDATED_SCORE)
            .scoreTime(UPDATED_SCORE_TIME)
            .rankTime(UPDATED_RANK_TIME)
            .displayClient(UPDATED_DISPLAY_CLIENT);

        restPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlayer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlayer))
            )
            .andExpect(status().isOk());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);
        Player testPlayer = playerList.get(playerList.size() - 1);
        assertThat(testPlayer.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testPlayer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPlayer.getRank()).isEqualTo(UPDATED_RANK);
        assertThat(testPlayer.getScore()).isEqualTo(UPDATED_SCORE);
        assertThat(testPlayer.getScoreTime()).isEqualTo(UPDATED_SCORE_TIME);
        assertThat(testPlayer.getRankTime()).isEqualTo(UPDATED_RANK_TIME);
        assertThat(testPlayer.getDisplayClient()).isEqualTo(UPDATED_DISPLAY_CLIENT);
    }

    @Test
    @Transactional
    void patchNonExistingPlayer() throws Exception {
        int databaseSizeBeforeUpdate = playerRepository.findAll().size();
        player.setId(count.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, playerDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(playerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlayer() throws Exception {
        int databaseSizeBeforeUpdate = playerRepository.findAll().size();
        player.setId(count.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(playerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlayer() throws Exception {
        int databaseSizeBeforeUpdate = playerRepository.findAll().size();
        player.setId(count.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(playerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlayer() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        int databaseSizeBeforeDelete = playerRepository.findAll().size();

        // Delete the player
        restPlayerMockMvc
            .perform(delete(ENTITY_API_URL_ID, player.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
