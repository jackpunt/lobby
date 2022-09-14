package com.thegraid.lobby.service.mapper;

import com.thegraid.lobby.domain.GameClass;
import com.thegraid.lobby.domain.GameInst;
import com.thegraid.lobby.domain.Player;
import com.thegraid.lobby.service.dto.GameClassDTO;
import com.thegraid.lobby.service.dto.GameInstDTO;
import com.thegraid.lobby.service.dto.PlayerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GameInst} and its DTO {@link GameInstDTO}.
 */
@Mapper(componentModel = "spring")
public interface GameInstMapper extends EntityMapper<GameInstDTO, GameInst> {
    @Mapping(target = "playerA", source = "playerA", qualifiedByName = "playerId")
    @Mapping(target = "playerB", source = "playerB", qualifiedByName = "playerId")
    @Mapping(target = "gameClass", source = "gameClass", qualifiedByName = "gameClassId")
    GameInstDTO toDto(GameInst s);

    @Named("playerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PlayerDTO toDtoPlayerId(Player player);

    @Named("gameClassId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GameClassDTO toDtoGameClassId(GameClass gameClass);
}
