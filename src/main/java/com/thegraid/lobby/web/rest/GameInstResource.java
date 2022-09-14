package com.thegraid.lobby.web.rest;

import com.thegraid.lobby.repository.GameInstRepository;
import com.thegraid.lobby.service.GameInstService;
import com.thegraid.lobby.service.dto.GameInstDTO;
import com.thegraid.lobby.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
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
 * REST controller for managing {@link com.thegraid.lobby.domain.GameInst}.
 */
@RestController
@RequestMapping("/api")
public class GameInstResource {

    private final Logger log = LoggerFactory.getLogger(GameInstResource.class);

    private static final String ENTITY_NAME = "gameInst";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GameInstService gameInstService;

    private final GameInstRepository gameInstRepository;

    public GameInstResource(GameInstService gameInstService, GameInstRepository gameInstRepository) {
        this.gameInstService = gameInstService;
        this.gameInstRepository = gameInstRepository;
    }

    /**
     * {@code POST  /game-insts} : Create a new gameInst.
     *
     * @param gameInstDTO the gameInstDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new gameInstDTO, or with status {@code 400 (Bad Request)} if the gameInst has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/game-insts")
    public ResponseEntity<GameInstDTO> createGameInst(@Valid @RequestBody GameInstDTO gameInstDTO) throws URISyntaxException {
        log.debug("REST request to save GameInst : {}", gameInstDTO);
        if (gameInstDTO.getId() != null) {
            throw new BadRequestAlertException("A new gameInst cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GameInstDTO result = gameInstService.save(gameInstDTO);
        return ResponseEntity
            .created(new URI("/api/game-insts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /game-insts/:id} : Updates an existing gameInst.
     *
     * @param id the id of the gameInstDTO to save.
     * @param gameInstDTO the gameInstDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gameInstDTO,
     * or with status {@code 400 (Bad Request)} if the gameInstDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the gameInstDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/game-insts/{id}")
    public ResponseEntity<GameInstDTO> updateGameInst(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GameInstDTO gameInstDTO
    ) throws URISyntaxException {
        log.debug("REST request to update GameInst : {}, {}", id, gameInstDTO);
        if (gameInstDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gameInstDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gameInstRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GameInstDTO result = gameInstService.update(gameInstDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gameInstDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /game-insts/:id} : Partial updates given fields of an existing gameInst, field will ignore if it is null
     *
     * @param id the id of the gameInstDTO to save.
     * @param gameInstDTO the gameInstDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gameInstDTO,
     * or with status {@code 400 (Bad Request)} if the gameInstDTO is not valid,
     * or with status {@code 404 (Not Found)} if the gameInstDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the gameInstDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/game-insts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GameInstDTO> partialUpdateGameInst(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GameInstDTO gameInstDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update GameInst partially : {}, {}", id, gameInstDTO);
        if (gameInstDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gameInstDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gameInstRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GameInstDTO> result = gameInstService.partialUpdate(gameInstDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gameInstDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /game-insts} : get all the gameInsts.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of gameInsts in body.
     */
    @GetMapping("/game-insts")
    public List<GameInstDTO> getAllGameInsts(@RequestParam(required = false) String filter) {
        if ("props-is-null".equals(filter)) {
            log.debug("REST request to get all GameInsts where props is null");
            return gameInstService.findAllWherePropsIsNull();
        }
        log.debug("REST request to get all GameInsts");
        return gameInstService.findAll();
    }

    /**
     * {@code GET  /game-insts/:id} : get the "id" gameInst.
     *
     * @param id the id of the gameInstDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the gameInstDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/game-insts/{id}")
    public ResponseEntity<GameInstDTO> getGameInst(@PathVariable Long id) {
        log.debug("REST request to get GameInst : {}", id);
        Optional<GameInstDTO> gameInstDTO = gameInstService.findOne(id);
        return ResponseUtil.wrapOrNotFound(gameInstDTO);
    }

    /**
     * {@code DELETE  /game-insts/:id} : delete the "id" gameInst.
     *
     * @param id the id of the gameInstDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/game-insts/{id}")
    public ResponseEntity<Void> deleteGameInst(@PathVariable Long id) {
        log.debug("REST request to delete GameInst : {}", id);
        gameInstService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
