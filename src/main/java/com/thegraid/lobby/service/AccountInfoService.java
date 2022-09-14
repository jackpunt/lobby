package com.thegraid.lobby.service;

import com.thegraid.lobby.domain.AccountInfo;
import com.thegraid.lobby.repository.AccountInfoRepository;
import com.thegraid.lobby.repository.UserRepository;
import com.thegraid.lobby.service.dto.AccountInfoDTO;
import com.thegraid.lobby.service.mapper.AccountInfoMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AccountInfo}.
 */
@Service
@Transactional
public class AccountInfoService {

    private final Logger log = LoggerFactory.getLogger(AccountInfoService.class);

    private final AccountInfoRepository accountInfoRepository;

    private final AccountInfoMapper accountInfoMapper;

    private final UserRepository userRepository;

    public AccountInfoService(
        AccountInfoRepository accountInfoRepository,
        AccountInfoMapper accountInfoMapper,
        UserRepository userRepository
    ) {
        this.accountInfoRepository = accountInfoRepository;
        this.accountInfoMapper = accountInfoMapper;
        this.userRepository = userRepository;
    }

    /**
     * Save a accountInfo.
     *
     * @param accountInfoDTO the entity to save.
     * @return the persisted entity.
     */
    public AccountInfoDTO save(AccountInfoDTO accountInfoDTO) {
        log.debug("Request to save AccountInfo : {}", accountInfoDTO);
        AccountInfo accountInfo = accountInfoMapper.toEntity(accountInfoDTO);
        Long userId = accountInfoDTO.getUser().getId();
        userRepository.findById(userId).ifPresent(accountInfo::user);
        accountInfo = accountInfoRepository.save(accountInfo);
        return accountInfoMapper.toDto(accountInfo);
    }

    /**
     * Update a accountInfo.
     *
     * @param accountInfoDTO the entity to save.
     * @return the persisted entity.
     */
    public AccountInfoDTO update(AccountInfoDTO accountInfoDTO) {
        log.debug("Request to save AccountInfo : {}", accountInfoDTO);
        AccountInfo accountInfo = accountInfoMapper.toEntity(accountInfoDTO);
        Long userId = accountInfoDTO.getUser().getId();
        userRepository.findById(userId).ifPresent(accountInfo::user);
        accountInfo = accountInfoRepository.save(accountInfo);
        return accountInfoMapper.toDto(accountInfo);
    }

    /**
     * Partially update a accountInfo.
     *
     * @param accountInfoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AccountInfoDTO> partialUpdate(AccountInfoDTO accountInfoDTO) {
        log.debug("Request to partially update AccountInfo : {}", accountInfoDTO);

        return accountInfoRepository
            .findById(accountInfoDTO.getId())
            .map(existingAccountInfo -> {
                accountInfoMapper.partialUpdate(existingAccountInfo, accountInfoDTO);

                return existingAccountInfo;
            })
            .map(accountInfoRepository::save)
            .map(accountInfoMapper::toDto);
    }

    /**
     * Get all the accountInfos.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AccountInfoDTO> findAll() {
        log.debug("Request to get all AccountInfos");
        return accountInfoRepository.findAll().stream().map(accountInfoMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one accountInfo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AccountInfoDTO> findOne(Long id) {
        log.debug("Request to get AccountInfo : {}", id);
        return accountInfoRepository.findById(id).map(accountInfoMapper::toDto);
    }

    /**
     * Delete the accountInfo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete AccountInfo : {}", id);
        accountInfoRepository.deleteById(id);
    }
}
