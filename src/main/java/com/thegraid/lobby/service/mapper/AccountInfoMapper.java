package com.thegraid.lobby.service.mapper;

import com.thegraid.lobby.domain.AccountInfo;
import com.thegraid.lobby.domain.User;
import com.thegraid.lobby.service.dto.AccountInfoDTO;
import com.thegraid.lobby.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AccountInfo} and its DTO {@link AccountInfoDTO}.
 */
@Mapper(componentModel = "spring")
public interface AccountInfoMapper extends EntityMapper<AccountInfoDTO, AccountInfo> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    AccountInfoDTO toDto(AccountInfo s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
