package com.thegraid.lobby.service;

import com.thegraid.lobby.domain.GameInst;
import com.thegraid.lobby.repository.GameInstRepository;
import com.thegraid.lobby.service.dto.GameInstDTO;
import com.thegraid.lobby.service.mapper.GameInstMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link GameInst}.
 */
@Service
@Transactional
public class GameInstService {

    private final Logger log = LoggerFactory.getLogger(GameInstService.class);

    private final GameInstRepository gameInstRepository;

    private final GameInstMapper gameInstMapper;

    public GameInstService(GameInstRepository gameInstRepository, GameInstMapper gameInstMapper) {
        this.gameInstRepository = gameInstRepository;
        this.gameInstMapper = gameInstMapper;
    }

    /**
     * Save a gameInst.
     *
     * @param gameInstDTO the entity to save.
     * @return the persisted entity.
     */
    public GameInstDTO save(GameInstDTO gameInstDTO) {
        log.debug("Request to save GameInst : {}", gameInstDTO);
        GameInst gameInst = gameInstMapper.toEntity(gameInstDTO);
        gameInst = gameInstRepository.save(gameInst);
        return gameInstMapper.toDto(gameInst);
    }

    /**
     * Update a gameInst.
     *
     * @param gameInstDTO the entity to save.
     * @return the persisted entity.
     */
    public GameInstDTO update(GameInstDTO gameInstDTO) {
        log.debug("Request to save GameInst : {}", gameInstDTO);
        GameInst gameInst = gameInstMapper.toEntity(gameInstDTO);
        gameInst = gameInstRepository.save(gameInst);
        return gameInstMapper.toDto(gameInst);
    }

    /**
     * Partially update a gameInst.
     *
     * @param gameInstDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<GameInstDTO> partialUpdate(GameInstDTO gameInstDTO) {
        log.debug("Request to partially update GameInst : {}", gameInstDTO);

        return gameInstRepository
            .findById(gameInstDTO.getId())
            .map(existingGameInst -> {
                gameInstMapper.partialUpdate(existingGameInst, gameInstDTO);

                return existingGameInst;
            })
            .map(gameInstRepository::save)
            .map(gameInstMapper::toDto);
    }

    /**
     * Get all the gameInsts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<GameInstDTO> findAll() {
        log.debug("Request to get all GameInsts");
        return gameInstRepository.findAll().stream().map(gameInstMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the gameInsts where Props is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<GameInstDTO> findAllWherePropsIsNull() {
        log.debug("Request to get all gameInsts where Props is null");
        return StreamSupport
            .stream(gameInstRepository.findAll().spliterator(), false)
            .filter(gameInst -> gameInst.getProps() == null)
            .map(gameInstMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one gameInst by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GameInstDTO> findOne(Long id) {
        log.debug("Request to get GameInst : {}", id);
        return gameInstRepository.findById(id).map(gameInstMapper::toDto);
    }

    /**
     * Delete the gameInst by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete GameInst : {}", id);
        gameInstRepository.deleteById(id);
    }
}
