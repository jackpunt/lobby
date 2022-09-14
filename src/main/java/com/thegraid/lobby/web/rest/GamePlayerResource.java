package com.thegraid.lobby.web.rest;

import com.thegraid.lobby.repository.GamePlayerRepository;
import com.thegraid.lobby.service.GamePlayerService;
import com.thegraid.lobby.service.dto.GamePlayerDTO;
import com.thegraid.lobby.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.thegraid.lobby.domain.GamePlayer}.
 */
@RestController
@RequestMapping("/api")
public class GamePlayerResource {

    private final Logger log = LoggerFactory.getLogger(GamePlayerResource.class);

    private static final String ENTITY_NAME = "gamePlayer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GamePlayerService gamePlayerService;

    private final GamePlayerRepository gamePlayerRepository;

    public GamePlayerResource(GamePlayerService gamePlayerService, GamePlayerRepository gamePlayerRepository) {
        this.gamePlayerService = gamePlayerService;
        this.gamePlayerRepository = gamePlayerRepository;
    }

    /**
     * {@code POST  /game-players} : Create a new gamePlayer.
     *
     * @param gamePlayerDTO the gamePlayerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new gamePlayerDTO, or with status {@code 400 (Bad Request)} if the gamePlayer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/game-players")
    public ResponseEntity<GamePlayerDTO> createGamePlayer(@Valid @RequestBody GamePlayerDTO gamePlayerDTO) throws URISyntaxException {
        log.debug("REST request to save GamePlayer : {}", gamePlayerDTO);
        if (gamePlayerDTO.getId() != null) {
            throw new BadRequestAlertException("A new gamePlayer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GamePlayerDTO result = gamePlayerService.save(gamePlayerDTO);
        return ResponseEntity
            .created(new URI("/api/game-players/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /game-players/:id} : Updates an existing gamePlayer.
     *
     * @param id the id of the gamePlayerDTO to save.
     * @param gamePlayerDTO the gamePlayerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gamePlayerDTO,
     * or with status {@code 400 (Bad Request)} if the gamePlayerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the gamePlayerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/game-players/{id}")
    public ResponseEntity<GamePlayerDTO> updateGamePlayer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GamePlayerDTO gamePlayerDTO
    ) throws URISyntaxException {
        log.debug("REST request to update GamePlayer : {}, {}", id, gamePlayerDTO);
        if (gamePlayerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gamePlayerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gamePlayerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GamePlayerDTO result = gamePlayerService.update(gamePlayerDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gamePlayerDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /game-players/:id} : Partial updates given fields of an existing gamePlayer, field will ignore if it is null
     *
     * @param id the id of the gamePlayerDTO to save.
     * @param gamePlayerDTO the gamePlayerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gamePlayerDTO,
     * or with status {@code 400 (Bad Request)} if the gamePlayerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the gamePlayerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the gamePlayerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/game-players/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GamePlayerDTO> partialUpdateGamePlayer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GamePlayerDTO gamePlayerDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update GamePlayer partially : {}, {}", id, gamePlayerDTO);
        if (gamePlayerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gamePlayerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gamePlayerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GamePlayerDTO> result = gamePlayerService.partialUpdate(gamePlayerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gamePlayerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /game-players} : get all the gamePlayers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of gamePlayers in body.
     */
    @GetMapping("/game-players")
    public List<GamePlayerDTO> getAllGamePlayers() {
        log.debug("REST request to get all GamePlayers");
        return gamePlayerService.findAll();
    }

    /**
     * {@code GET  /game-players/:id} : get the "id" gamePlayer.
     *
     * @param id the id of the gamePlayerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the gamePlayerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/game-players/{id}")
    public ResponseEntity<GamePlayerDTO> getGamePlayer(@PathVariable Long id) {
        log.debug("REST request to get GamePlayer : {}", id);
        Optional<GamePlayerDTO> gamePlayerDTO = gamePlayerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(gamePlayerDTO);
    }

    /**
     * {@code DELETE  /game-players/:id} : delete the "id" gamePlayer.
     *
     * @param id the id of the gamePlayerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/game-players/{id}")
    public ResponseEntity<Void> deleteGamePlayer(@PathVariable Long id) {
        log.debug("REST request to delete GamePlayer : {}", id);
        gamePlayerService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
