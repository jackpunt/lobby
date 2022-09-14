package com.thegraid.lobby.service.mapper;

import com.thegraid.lobby.domain.GameClass;
import com.thegraid.lobby.domain.MemberGameProps;
import com.thegraid.lobby.domain.User;
import com.thegraid.lobby.service.dto.GameClassDTO;
import com.thegraid.lobby.service.dto.MemberGamePropsDTO;
import com.thegraid.lobby.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MemberGameProps} and its DTO {@link MemberGamePropsDTO}.
 */
@Mapper(componentModel = "spring")
public interface MemberGamePropsMapper extends EntityMapper<MemberGamePropsDTO, MemberGameProps> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "gameClass", source = "gameClass", qualifiedByName = "gameClassId")
    MemberGamePropsDTO toDto(MemberGameProps s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("gameClassId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GameClassDTO toDtoGameClassId(GameClass gameClass);
}
