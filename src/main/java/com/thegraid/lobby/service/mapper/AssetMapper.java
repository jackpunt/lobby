package com.thegraid.lobby.service.mapper;

import com.thegraid.lobby.domain.Asset;
import com.thegraid.lobby.domain.User;
import com.thegraid.lobby.service.dto.AssetDTO;
import com.thegraid.lobby.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Asset} and its DTO {@link AssetDTO}.
 */
@Mapper(componentModel = "spring")
public interface AssetMapper extends EntityMapper<AssetDTO, Asset> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    AssetDTO toDto(Asset s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
