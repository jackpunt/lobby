package com.thegraid.lobby.web.rest;

import com.thegraid.lobby.repository.AccountInfoRepository;
import com.thegraid.lobby.service.AccountInfoService;
import com.thegraid.lobby.service.dto.AccountInfoDTO;
import com.thegraid.lobby.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.thegraid.lobby.domain.AccountInfo}.
 */
@RestController
@RequestMapping("/api")
public class AccountInfoResource {

    private final Logger log = LoggerFactory.getLogger(AccountInfoResource.class);

    private static final String ENTITY_NAME = "accountInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AccountInfoService accountInfoService;

    private final AccountInfoRepository accountInfoRepository;

    public AccountInfoResource(AccountInfoService accountInfoService, AccountInfoRepository accountInfoRepository) {
        this.accountInfoService = accountInfoService;
        this.accountInfoRepository = accountInfoRepository;
    }

    /**
     * {@code POST  /account-infos} : Create a new accountInfo.
     *
     * @param accountInfoDTO the accountInfoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new accountInfoDTO, or with status {@code 400 (Bad Request)} if the accountInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/account-infos")
    public ResponseEntity<AccountInfoDTO> createAccountInfo(@RequestBody AccountInfoDTO accountInfoDTO) throws URISyntaxException {
        log.debug("REST request to save AccountInfo : {}", accountInfoDTO);
        if (accountInfoDTO.getId() != null) {
            throw new BadRequestAlertException("A new accountInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(accountInfoDTO.getUser())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        AccountInfoDTO result = accountInfoService.save(accountInfoDTO);
        return ResponseEntity
            .created(new URI("/api/account-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /account-infos/:id} : Updates an existing accountInfo.
     *
     * @param id the id of the accountInfoDTO to save.
     * @param accountInfoDTO the accountInfoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accountInfoDTO,
     * or with status {@code 400 (Bad Request)} if the accountInfoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the accountInfoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/account-infos/{id}")
    public ResponseEntity<AccountInfoDTO> updateAccountInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AccountInfoDTO accountInfoDTO
    ) throws URISyntaxException {
        log.debug("REST request to update AccountInfo : {}, {}", id, accountInfoDTO);
        if (accountInfoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accountInfoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accountInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AccountInfoDTO result = accountInfoService.update(accountInfoDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, accountInfoDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /account-infos/:id} : Partial updates given fields of an existing accountInfo, field will ignore if it is null
     *
     * @param id the id of the accountInfoDTO to save.
     * @param accountInfoDTO the accountInfoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accountInfoDTO,
     * or with status {@code 400 (Bad Request)} if the accountInfoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the accountInfoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the accountInfoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/account-infos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AccountInfoDTO> partialUpdateAccountInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AccountInfoDTO accountInfoDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update AccountInfo partially : {}, {}", id, accountInfoDTO);
        if (accountInfoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accountInfoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accountInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AccountInfoDTO> result = accountInfoService.partialUpdate(accountInfoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, accountInfoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /account-infos} : get all the accountInfos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of accountInfos in body.
     */
    @GetMapping("/account-infos")
    public List<AccountInfoDTO> getAllAccountInfos() {
        log.debug("REST request to get all AccountInfos");
        return accountInfoService.findAll();
    }

    /**
     * {@code GET  /account-infos/:id} : get the "id" accountInfo.
     *
     * @param id the id of the accountInfoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the accountInfoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/account-infos/{id}")
    public ResponseEntity<AccountInfoDTO> getAccountInfo(@PathVariable Long id) {
        log.debug("REST request to get AccountInfo : {}", id);
        Optional<AccountInfoDTO> accountInfoDTO = accountInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(accountInfoDTO);
    }

    /**
     * {@code DELETE  /account-infos/:id} : delete the "id" accountInfo.
     *
     * @param id the id of the accountInfoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/account-infos/{id}")
    public ResponseEntity<Void> deleteAccountInfo(@PathVariable Long id) {
        log.debug("REST request to delete AccountInfo : {}", id);
        accountInfoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
