package com.thegraid.lobby.service;

import com.thegraid.lobby.domain.GameInstProps;
import com.thegraid.lobby.repository.GameInstPropsRepository;
import com.thegraid.lobby.repository.GameInstRepository;
import com.thegraid.lobby.service.dto.GameInstPropsDTO;
import com.thegraid.lobby.service.mapper.GameInstPropsMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link GameInstProps}.
 */
@Service
@Transactional
public class GameInstPropsService {

    private final Logger log = LoggerFactory.getLogger(GameInstPropsService.class);

    private final GameInstPropsRepository gameInstPropsRepository;

    private final GameInstPropsMapper gameInstPropsMapper;

    private final GameInstRepository gameInstRepository;

    public GameInstPropsService(
        GameInstPropsRepository gameInstPropsRepository,
        GameInstPropsMapper gameInstPropsMapper,
        GameInstRepository gameInstRepository
    ) {
        this.gameInstPropsRepository = gameInstPropsRepository;
        this.gameInstPropsMapper = gameInstPropsMapper;
        this.gameInstRepository = gameInstRepository;
    }

    /**
     * Save a gameInstProps.
     *
     * @param gameInstPropsDTO the entity to save.
     * @return the persisted entity.
     */
    public GameInstPropsDTO save(GameInstPropsDTO gameInstPropsDTO) {
        log.debug("Request to save GameInstProps : {}", gameInstPropsDTO);
        GameInstProps gameInstProps = gameInstPropsMapper.toEntity(gameInstPropsDTO);
        Long gameInstId = gameInstPropsDTO.getGameInst().getId();
        gameInstRepository.findById(gameInstId).ifPresent(gameInstProps::gameInst);
        gameInstProps = gameInstPropsRepository.save(gameInstProps);
        return gameInstPropsMapper.toDto(gameInstProps);
    }

    /**
     * Update a gameInstProps.
     *
     * @param gameInstPropsDTO the entity to save.
     * @return the persisted entity.
     */
    public GameInstPropsDTO update(GameInstPropsDTO gameInstPropsDTO) {
        log.debug("Request to save GameInstProps : {}", gameInstPropsDTO);
        GameInstProps gameInstProps = gameInstPropsMapper.toEntity(gameInstPropsDTO);
        Long gameInstId = gameInstPropsDTO.getGameInst().getId();
        gameInstRepository.findById(gameInstId).ifPresent(gameInstProps::gameInst);
        gameInstProps = gameInstPropsRepository.save(gameInstProps);
        return gameInstPropsMapper.toDto(gameInstProps);
    }

    /**
     * Partially update a gameInstProps.
     *
     * @param gameInstPropsDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<GameInstPropsDTO> partialUpdate(GameInstPropsDTO gameInstPropsDTO) {
        log.debug("Request to partially update GameInstProps : {}", gameInstPropsDTO);

        return gameInstPropsRepository
            .findById(gameInstPropsDTO.getId())
            .map(existingGameInstProps -> {
                gameInstPropsMapper.partialUpdate(existingGameInstProps, gameInstPropsDTO);

                return existingGameInstProps;
            })
            .map(gameInstPropsRepository::save)
            .map(gameInstPropsMapper::toDto);
    }

    /**
     * Get all the gameInstProps.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<GameInstPropsDTO> findAll() {
        log.debug("Request to get all GameInstProps");
        return gameInstPropsRepository.findAll().stream().map(gameInstPropsMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one gameInstProps by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GameInstPropsDTO> findOne(Long id) {
        log.debug("Request to get GameInstProps : {}", id);
        return gameInstPropsRepository.findById(id).map(gameInstPropsMapper::toDto);
    }

    /**
     * Delete the gameInstProps by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete GameInstProps : {}", id);
        gameInstPropsRepository.deleteById(id);
    }
}
