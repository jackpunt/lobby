package com.thegraid.lobby.service;

import com.thegraid.lobby.domain.MemberGameProps;
import com.thegraid.lobby.repository.MemberGamePropsRepository;
import com.thegraid.lobby.service.dto.MemberGamePropsDTO;
import com.thegraid.lobby.service.mapper.MemberGamePropsMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link MemberGameProps}.
 */
@Service
@Transactional
public class MemberGamePropsService {

    private final Logger log = LoggerFactory.getLogger(MemberGamePropsService.class);

    private final MemberGamePropsRepository memberGamePropsRepository;

    private final MemberGamePropsMapper memberGamePropsMapper;

    public MemberGamePropsService(MemberGamePropsRepository memberGamePropsRepository, MemberGamePropsMapper memberGamePropsMapper) {
        this.memberGamePropsRepository = memberGamePropsRepository;
        this.memberGamePropsMapper = memberGamePropsMapper;
    }

    /**
     * Save a memberGameProps.
     *
     * @param memberGamePropsDTO the entity to save.
     * @return the persisted entity.
     */
    public MemberGamePropsDTO save(MemberGamePropsDTO memberGamePropsDTO) {
        log.debug("Request to save MemberGameProps : {}", memberGamePropsDTO);
        MemberGameProps memberGameProps = memberGamePropsMapper.toEntity(memberGamePropsDTO);
        memberGameProps = memberGamePropsRepository.save(memberGameProps);
        return memberGamePropsMapper.toDto(memberGameProps);
    }

    /**
     * Update a memberGameProps.
     *
     * @param memberGamePropsDTO the entity to save.
     * @return the persisted entity.
     */
    public MemberGamePropsDTO update(MemberGamePropsDTO memberGamePropsDTO) {
        log.debug("Request to save MemberGameProps : {}", memberGamePropsDTO);
        MemberGameProps memberGameProps = memberGamePropsMapper.toEntity(memberGamePropsDTO);
        memberGameProps = memberGamePropsRepository.save(memberGameProps);
        return memberGamePropsMapper.toDto(memberGameProps);
    }

    /**
     * Partially update a memberGameProps.
     *
     * @param memberGamePropsDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MemberGamePropsDTO> partialUpdate(MemberGamePropsDTO memberGamePropsDTO) {
        log.debug("Request to partially update MemberGameProps : {}", memberGamePropsDTO);

        return memberGamePropsRepository
            .findById(memberGamePropsDTO.getId())
            .map(existingMemberGameProps -> {
                memberGamePropsMapper.partialUpdate(existingMemberGameProps, memberGamePropsDTO);

                return existingMemberGameProps;
            })
            .map(memberGamePropsRepository::save)
            .map(memberGamePropsMapper::toDto);
    }

    /**
     * Get all the memberGameProps.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MemberGamePropsDTO> findAll() {
        log.debug("Request to get all MemberGameProps");
        return memberGamePropsRepository
            .findAll()
            .stream()
            .map(memberGamePropsMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one memberGameProps by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MemberGamePropsDTO> findOne(Long id) {
        log.debug("Request to get MemberGameProps : {}", id);
        return memberGamePropsRepository.findById(id).map(memberGamePropsMapper::toDto);
    }

    /**
     * Delete the memberGameProps by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete MemberGameProps : {}", id);
        memberGamePropsRepository.deleteById(id);
    }
}
