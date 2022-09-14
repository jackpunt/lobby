package com.thegraid.lobby.service.mapper;

import com.thegraid.lobby.domain.GameClass;
import com.thegraid.lobby.service.dto.GameClassDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GameClass} and its DTO {@link GameClassDTO}.
 */
@Mapper(componentModel = "spring")
public interface GameClassMapper extends EntityMapper<GameClassDTO, GameClass> {}
