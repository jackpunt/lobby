package com.thegraid.lobby.service;

import com.thegraid.lobby.domain.GamePlayer;
import com.thegraid.lobby.repository.GamePlayerRepository;
import com.thegraid.lobby.service.dto.GamePlayerDTO;
import com.thegraid.lobby.service.mapper.GamePlayerMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link GamePlayer}.
 */
@Service
@Transactional
public class GamePlayerService {

    private final Logger log = LoggerFactory.getLogger(GamePlayerService.class);

    private final GamePlayerRepository gamePlayerRepository;

    private final GamePlayerMapper gamePlayerMapper;

    public GamePlayerService(GamePlayerRepository gamePlayerRepository, GamePlayerMapper gamePlayerMapper) {
        this.gamePlayerRepository = gamePlayerRepository;
        this.gamePlayerMapper = gamePlayerMapper;
    }

    /**
     * Save a gamePlayer.
     *
     * @param gamePlayerDTO the entity to save.
     * @return the persisted entity.
     */
    public GamePlayerDTO save(GamePlayerDTO gamePlayerDTO) {
        log.debug("Request to save GamePlayer : {}", gamePlayerDTO);
        GamePlayer gamePlayer = gamePlayerMapper.toEntity(gamePlayerDTO);
        gamePlayer = gamePlayerRepository.save(gamePlayer);
        return gamePlayerMapper.toDto(gamePlayer);
    }

    /**
     * Update a gamePlayer.
     *
     * @param gamePlayerDTO the entity to save.
     * @return the persisted entity.
     */
    public GamePlayerDTO update(GamePlayerDTO gamePlayerDTO) {
        log.debug("Request to save GamePlayer : {}", gamePlayerDTO);
        GamePlayer gamePlayer = gamePlayerMapper.toEntity(gamePlayerDTO);
        gamePlayer = gamePlayerRepository.save(gamePlayer);
        return gamePlayerMapper.toDto(gamePlayer);
    }

    /**
     * Partially update a gamePlayer.
     *
     * @param gamePlayerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<GamePlayerDTO> partialUpdate(GamePlayerDTO gamePlayerDTO) {
        log.debug("Request to partially update GamePlayer : {}", gamePlayerDTO);

        return gamePlayerRepository
            .findById(gamePlayerDTO.getId())
            .map(existingGamePlayer -> {
                gamePlayerMapper.partialUpdate(existingGamePlayer, gamePlayerDTO);

                return existingGamePlayer;
            })
            .map(gamePlayerRepository::save)
            .map(gamePlayerMapper::toDto);
    }

    /**
     * Get all the gamePlayers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<GamePlayerDTO> findAll() {
        log.debug("Request to get all GamePlayers");
        return gamePlayerRepository.findAll().stream().map(gamePlayerMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one gamePlayer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GamePlayerDTO> findOne(Long id) {
        log.debug("Request to get GamePlayer : {}", id);
        return gamePlayerRepository.findById(id).map(gamePlayerMapper::toDto);
    }

    /**
     * Delete the gamePlayer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete GamePlayer : {}", id);
        gamePlayerRepository.deleteById(id);
    }
}
