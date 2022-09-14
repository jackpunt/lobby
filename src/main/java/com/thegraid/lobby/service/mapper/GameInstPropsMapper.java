package com.thegraid.lobby.service.mapper;

import com.thegraid.lobby.domain.GameInst;
import com.thegraid.lobby.domain.GameInstProps;
import com.thegraid.lobby.service.dto.GameInstDTO;
import com.thegraid.lobby.service.dto.GameInstPropsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GameInstProps} and its DTO {@link GameInstPropsDTO}.
 */
@Mapper(componentModel = "spring")
public interface GameInstPropsMapper extends EntityMapper<GameInstPropsDTO, GameInstProps> {
    @Mapping(target = "gameInst", source = "gameInst", qualifiedByName = "gameInstId")
    GameInstPropsDTO toDto(GameInstProps s);

    @Named("gameInstId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GameInstDTO toDtoGameInstId(GameInst gameInst);
}
