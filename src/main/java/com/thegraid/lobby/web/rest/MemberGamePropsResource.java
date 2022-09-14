package com.thegraid.lobby.web.rest;

import com.thegraid.lobby.repository.MemberGamePropsRepository;
import com.thegraid.lobby.service.MemberGamePropsService;
import com.thegraid.lobby.service.dto.MemberGamePropsDTO;
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
 * REST controller for managing {@link com.thegraid.lobby.domain.MemberGameProps}.
 */
@RestController
@RequestMapping("/api")
public class MemberGamePropsResource {

    private final Logger log = LoggerFactory.getLogger(MemberGamePropsResource.class);

    private static final String ENTITY_NAME = "memberGameProps";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MemberGamePropsService memberGamePropsService;

    private final MemberGamePropsRepository memberGamePropsRepository;

    public MemberGamePropsResource(MemberGamePropsService memberGamePropsService, MemberGamePropsRepository memberGamePropsRepository) {
        this.memberGamePropsService = memberGamePropsService;
        this.memberGamePropsRepository = memberGamePropsRepository;
    }

    /**
     * {@code POST  /member-game-props} : Create a new memberGameProps.
     *
     * @param memberGamePropsDTO the memberGamePropsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new memberGamePropsDTO, or with status {@code 400 (Bad Request)} if the memberGameProps has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/member-game-props")
    public ResponseEntity<MemberGamePropsDTO> createMemberGameProps(@Valid @RequestBody MemberGamePropsDTO memberGamePropsDTO)
        throws URISyntaxException {
        log.debug("REST request to save MemberGameProps : {}", memberGamePropsDTO);
        if (memberGamePropsDTO.getId() != null) {
            throw new BadRequestAlertException("A new memberGameProps cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MemberGamePropsDTO result = memberGamePropsService.save(memberGamePropsDTO);
        return ResponseEntity
            .created(new URI("/api/member-game-props/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /member-game-props/:id} : Updates an existing memberGameProps.
     *
     * @param id the id of the memberGamePropsDTO to save.
     * @param memberGamePropsDTO the memberGamePropsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated memberGamePropsDTO,
     * or with status {@code 400 (Bad Request)} if the memberGamePropsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the memberGamePropsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/member-game-props/{id}")
    public ResponseEntity<MemberGamePropsDTO> updateMemberGameProps(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MemberGamePropsDTO memberGamePropsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MemberGameProps : {}, {}", id, memberGamePropsDTO);
        if (memberGamePropsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, memberGamePropsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!memberGamePropsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        MemberGamePropsDTO result = memberGamePropsService.update(memberGamePropsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, memberGamePropsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /member-game-props/:id} : Partial updates given fields of an existing memberGameProps, field will ignore if it is null
     *
     * @param id the id of the memberGamePropsDTO to save.
     * @param memberGamePropsDTO the memberGamePropsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated memberGamePropsDTO,
     * or with status {@code 400 (Bad Request)} if the memberGamePropsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the memberGamePropsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the memberGamePropsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/member-game-props/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MemberGamePropsDTO> partialUpdateMemberGameProps(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MemberGamePropsDTO memberGamePropsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MemberGameProps partially : {}, {}", id, memberGamePropsDTO);
        if (memberGamePropsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, memberGamePropsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!memberGamePropsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MemberGamePropsDTO> result = memberGamePropsService.partialUpdate(memberGamePropsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, memberGamePropsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /member-game-props} : get all the memberGameProps.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of memberGameProps in body.
     */
    @GetMapping("/member-game-props")
    public List<MemberGamePropsDTO> getAllMemberGameProps() {
        log.debug("REST request to get all MemberGameProps");
        return memberGamePropsService.findAll();
    }

    /**
     * {@code GET  /member-game-props/:id} : get the "id" memberGameProps.
     *
     * @param id the id of the memberGamePropsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the memberGamePropsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/member-game-props/{id}")
    public ResponseEntity<MemberGamePropsDTO> getMemberGameProps(@PathVariable Long id) {
        log.debug("REST request to get MemberGameProps : {}", id);
        Optional<MemberGamePropsDTO> memberGamePropsDTO = memberGamePropsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(memberGamePropsDTO);
    }

    /**
     * {@code DELETE  /member-game-props/:id} : delete the "id" memberGameProps.
     *
     * @param id the id of the memberGamePropsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/member-game-props/{id}")
    public ResponseEntity<Void> deleteMemberGameProps(@PathVariable Long id) {
        log.debug("REST request to delete MemberGameProps : {}", id);
        memberGamePropsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
