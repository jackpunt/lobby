package com.thegraid.lobby.web.rest;

import com.thegraid.lobby.repository.GameClassRepository;
import com.thegraid.lobby.service.GameClassService;
import com.thegraid.lobby.service.dto.GameClassDTO;
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
 * REST controller for managing {@link com.thegraid.lobby.domain.GameClass}.
 */
@RestController
@RequestMapping("/api")
public class GameClassResource {

    private final Logger log = LoggerFactory.getLogger(GameClassResource.class);

    private static final String ENTITY_NAME = "gameClass";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GameClassService gameClassService;

    private final GameClassRepository gameClassRepository;

    public GameClassResource(GameClassService gameClassService, GameClassRepository gameClassRepository) {
        this.gameClassService = gameClassService;
        this.gameClassRepository = gameClassRepository;
    }

    /**
     * {@code POST  /game-classes} : Create a new gameClass.
     *
     * @param gameClassDTO the gameClassDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new gameClassDTO, or with status {@code 400 (Bad Request)} if the gameClass has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/game-classes")
    public ResponseEntity<GameClassDTO> createGameClass(@Valid @RequestBody GameClassDTO gameClassDTO) throws URISyntaxException {
        log.debug("REST request to save GameClass : {}", gameClassDTO);
        if (gameClassDTO.getId() != null) {
            throw new BadRequestAlertException("A new gameClass cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GameClassDTO result = gameClassService.save(gameClassDTO);
        return ResponseEntity
            .created(new URI("/api/game-classes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /game-classes/:id} : Updates an existing gameClass.
     *
     * @param id the id of the gameClassDTO to save.
     * @param gameClassDTO the gameClassDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gameClassDTO,
     * or with status {@code 400 (Bad Request)} if the gameClassDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the gameClassDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/game-classes/{id}")
    public ResponseEntity<GameClassDTO> updateGameClass(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GameClassDTO gameClassDTO
    ) throws URISyntaxException {
        log.debug("REST request to update GameClass : {}, {}", id, gameClassDTO);
        if (gameClassDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gameClassDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gameClassRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GameClassDTO result = gameClassService.update(gameClassDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gameClassDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /game-classes/:id} : Partial updates given fields of an existing gameClass, field will ignore if it is null
     *
     * @param id the id of the gameClassDTO to save.
     * @param gameClassDTO the gameClassDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gameClassDTO,
     * or with status {@code 400 (Bad Request)} if the gameClassDTO is not valid,
     * or with status {@code 404 (Not Found)} if the gameClassDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the gameClassDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/game-classes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GameClassDTO> partialUpdateGameClass(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GameClassDTO gameClassDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update GameClass partially : {}, {}", id, gameClassDTO);
        if (gameClassDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gameClassDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gameClassRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GameClassDTO> result = gameClassService.partialUpdate(gameClassDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gameClassDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /game-classes} : get all the gameClasses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of gameClasses in body.
     */
    @GetMapping("/game-classes")
    public List<GameClassDTO> getAllGameClasses() {
        log.debug("REST request to get all GameClasses");
        return gameClassService.findAll();
    }

    /**
     * {@code GET  /game-classes/:id} : get the "id" gameClass.
     *
     * @param id the id of the gameClassDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the gameClassDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/game-classes/{id}")
    public ResponseEntity<GameClassDTO> getGameClass(@PathVariable Long id) {
        log.debug("REST request to get GameClass : {}", id);
        Optional<GameClassDTO> gameClassDTO = gameClassService.findOne(id);
        return ResponseUtil.wrapOrNotFound(gameClassDTO);
    }

    /**
     * {@code DELETE  /game-classes/:id} : delete the "id" gameClass.
     *
     * @param id the id of the gameClassDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/game-classes/{id}")
    public ResponseEntity<Void> deleteGameClass(@PathVariable Long id) {
        log.debug("REST request to delete GameClass : {}", id);
        gameClassService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
