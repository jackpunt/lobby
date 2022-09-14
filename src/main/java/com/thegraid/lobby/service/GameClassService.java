package com.thegraid.lobby.service;

import com.thegraid.lobby.domain.GameClass;
import com.thegraid.lobby.repository.GameClassRepository;
import com.thegraid.lobby.service.dto.GameClassDTO;
import com.thegraid.lobby.service.mapper.GameClassMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link GameClass}.
 */
@Service
@Transactional
public class GameClassService {

    private final Logger log = LoggerFactory.getLogger(GameClassService.class);

    private final GameClassRepository gameClassRepository;

    private final GameClassMapper gameClassMapper;

    public GameClassService(GameClassRepository gameClassRepository, GameClassMapper gameClassMapper) {
        this.gameClassRepository = gameClassRepository;
        this.gameClassMapper = gameClassMapper;
    }

    /**
     * Save a gameClass.
     *
     * @param gameClassDTO the entity to save.
     * @return the persisted entity.
     */
    public GameClassDTO save(GameClassDTO gameClassDTO) {
        log.debug("Request to save GameClass : {}", gameClassDTO);
        GameClass gameClass = gameClassMapper.toEntity(gameClassDTO);
        gameClass = gameClassRepository.save(gameClass);
        return gameClassMapper.toDto(gameClass);
    }

    /**
     * Update a gameClass.
     *
     * @param gameClassDTO the entity to save.
     * @return the persisted entity.
     */
    public GameClassDTO update(GameClassDTO gameClassDTO) {
        log.debug("Request to save GameClass : {}", gameClassDTO);
        GameClass gameClass = gameClassMapper.toEntity(gameClassDTO);
        gameClass = gameClassRepository.save(gameClass);
        return gameClassMapper.toDto(gameClass);
    }

    /**
     * Partially update a gameClass.
     *
     * @param gameClassDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<GameClassDTO> partialUpdate(GameClassDTO gameClassDTO) {
        log.debug("Request to partially update GameClass : {}", gameClassDTO);

        return gameClassRepository
            .findById(gameClassDTO.getId())
            .map(existingGameClass -> {
                gameClassMapper.partialUpdate(existingGameClass, gameClassDTO);

                return existingGameClass;
            })
            .map(gameClassRepository::save)
            .map(gameClassMapper::toDto);
    }

    /**
     * Get all the gameClasses.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<GameClassDTO> findAll() {
        log.debug("Request to get all GameClasses");
        return gameClassRepository.findAll().stream().map(gameClassMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one gameClass by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GameClassDTO> findOne(Long id) {
        log.debug("Request to get GameClass : {}", id);
        return gameClassRepository.findById(id).map(gameClassMapper::toDto);
    }

    /**
     * Delete the gameClass by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete GameClass : {}", id);
        gameClassRepository.deleteById(id);
    }
}
