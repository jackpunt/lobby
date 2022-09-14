package com.thegraid.lobby.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.thegraid.lobby.domain.AccountInfo} entity.
 */
@Schema(
    description = "extension to User (user is the owner of a stable of horses)\nAccount Type indicates the payment properties and the League user is in.\nmaybe this could fold into User.role ?"
)
public class AccountInfoDTO implements Serializable {

    private Long id;

    private Integer version;

    private String type;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccountInfoDTO)) {
            return false;
        }

        AccountInfoDTO accountInfoDTO = (AccountInfoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, accountInfoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AccountInfoDTO{" +
            "id=" + getId() +
            ", version=" + getVersion() +
            ", type='" + getType() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
