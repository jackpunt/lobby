package com.thegraid.lobby.web.rest;

import com.thegraid.lobby.repository.GameInstPropsRepository;
import com.thegraid.lobby.service.GameInstPropsService;
import com.thegraid.lobby.service.dto.GameInstPropsDTO;
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
 * REST controller for managing {@link com.thegraid.lobby.domain.GameInstProps}.
 */
@RestController
@RequestMapping("/api")
public class GameInstPropsResource {

    private final Logger log = LoggerFactory.getLogger(GameInstPropsResource.class);

    private static final String ENTITY_NAME = "gameInstProps";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GameInstPropsService gameInstPropsService;

    private final GameInstPropsRepository gameInstPropsRepository;

    public GameInstPropsResource(GameInstPropsService gameInstPropsService, GameInstPropsRepository gameInstPropsRepository) {
        this.gameInstPropsService = gameInstPropsService;
        this.gameInstPropsRepository = gameInstPropsRepository;
    }

    /**
     * {@code POST  /game-inst-props} : Create a new gameInstProps.
     *
     * @param gameInstPropsDTO the gameInstPropsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new gameInstPropsDTO, or with status {@code 400 (Bad Request)} if the gameInstProps has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/game-inst-props")
    public ResponseEntity<GameInstPropsDTO> createGameInstProps(@Valid @RequestBody GameInstPropsDTO gameInstPropsDTO)
        throws URISyntaxException {
        log.debug("REST request to save GameInstProps : {}", gameInstPropsDTO);
        if (gameInstPropsDTO.getId() != null) {
            throw new BadRequestAlertException("A new gameInstProps cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(gameInstPropsDTO.getGameInst())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        GameInstPropsDTO result = gameInstPropsService.save(gameInstPropsDTO);
        return ResponseEntity
            .created(new URI("/api/game-inst-props/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /game-inst-props/:id} : Updates an existing gameInstProps.
     *
     * @param id the id of the gameInstPropsDTO to save.
     * @param gameInstPropsDTO the gameInstPropsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gameInstPropsDTO,
     * or with status {@code 400 (Bad Request)} if the gameInstPropsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the gameInstPropsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/game-inst-props/{id}")
    public ResponseEntity<GameInstPropsDTO> updateGameInstProps(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GameInstPropsDTO gameInstPropsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update GameInstProps : {}, {}", id, gameInstPropsDTO);
        if (gameInstPropsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gameInstPropsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gameInstPropsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GameInstPropsDTO result = gameInstPropsService.update(gameInstPropsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gameInstPropsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /game-inst-props/:id} : Partial updates given fields of an existing gameInstProps, field will ignore if it is null
     *
     * @param id the id of the gameInstPropsDTO to save.
     * @param gameInstPropsDTO the gameInstPropsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gameInstPropsDTO,
     * or with status {@code 400 (Bad Request)} if the gameInstPropsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the gameInstPropsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the gameInstPropsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/game-inst-props/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GameInstPropsDTO> partialUpdateGameInstProps(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GameInstPropsDTO gameInstPropsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update GameInstProps partially : {}, {}", id, gameInstPropsDTO);
        if (gameInstPropsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gameInstPropsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gameInstPropsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GameInstPropsDTO> result = gameInstPropsService.partialUpdate(gameInstPropsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gameInstPropsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /game-inst-props} : get all the gameInstProps.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of gameInstProps in body.
     */
    @GetMapping("/game-inst-props")
    public List<GameInstPropsDTO> getAllGameInstProps() {
        log.debug("REST request to get all GameInstProps");
        return gameInstPropsService.findAll();
    }

    /**
     * {@code GET  /game-inst-props/:id} : get the "id" gameInstProps.
     *
     * @param id the id of the gameInstPropsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the gameInstPropsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/game-inst-props/{id}")
    public ResponseEntity<GameInstPropsDTO> getGameInstProps(@PathVariable Long id) {
        log.debug("REST request to get GameInstProps : {}", id);
        Optional<GameInstPropsDTO> gameInstPropsDTO = gameInstPropsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(gameInstPropsDTO);
    }

    /**
     * {@code DELETE  /game-inst-props/:id} : delete the "id" gameInstProps.
     *
     * @param id the id of the gameInstPropsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/game-inst-props/{id}")
    public ResponseEntity<Void> deleteGameInstProps(@PathVariable Long id) {
        log.debug("REST request to delete GameInstProps : {}", id);
        gameInstPropsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
