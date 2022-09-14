package com.thegraid.lobby.service.mapper;

import com.thegraid.lobby.domain.Asset;
import com.thegraid.lobby.domain.GameClass;
import com.thegraid.lobby.domain.Player;
import com.thegraid.lobby.domain.User;
import com.thegraid.lobby.service.dto.AssetDTO;
import com.thegraid.lobby.service.dto.GameClassDTO;
import com.thegraid.lobby.service.dto.PlayerDTO;
import com.thegraid.lobby.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Player} and its DTO {@link PlayerDTO}.
 */
@Mapper(componentModel = "spring")
public interface PlayerMapper extends EntityMapper<PlayerDTO, Player> {
    @Mapping(target = "gameClass", source = "gameClass", qualifiedByName = "gameClassId")
    @Mapping(target = "mainJar", source = "mainJar", qualifiedByName = "assetId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    PlayerDTO toDto(Player s);

    @Named("gameClassId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GameClassDTO toDtoGameClassId(GameClass gameClass);

    @Named("assetId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AssetDTO toDtoAssetId(Asset asset);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
