package com.thegraid.lobby.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.thegraid.lobby.domain.Asset} entity.
 */
@Schema(description = "Assets owned by a member/user; (the horses) a virtual file-system?")
public class AssetDTO implements Serializable {

    private Long id;

    private Integer version;

    /**
     * display name
     */
    @Size(max = 45)
    @Schema(description = "display name")
    private String name;

    /**
     * IPlayer - can be player.main_jar
     */
    @Schema(description = "IPlayer - can be player.main_jar")
    private Boolean main;

    /**
     * bot is full-auto
     */
    @Schema(description = "bot is full-auto")
    private Boolean auto;

    /**
     * url to asset (class or document/resource)\njar-path ! fqcn.of.asset.class\njar-path ! path/inside/jar/document\nURL=getenv(“ASSETBASE”)+path/to/release.jar ! user.supplied.Player
     */
    @Schema(
        description = "url to asset (class or document/resource)\njar-path ! fqcn.of.asset.class\njar-path ! path/inside/jar/document\nURL=getenv(“ASSETBASE”)+path/to/release.jar ! user.supplied.Player"
    )
    private String path;

    /**
     * comma-separated list of asset Ids
     */
    @Schema(description = "comma-separated list of asset Ids")
    private String include;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getMain() {
        return main;
    }

    public void setMain(Boolean main) {
        this.main = main;
    }

    public Boolean getAuto() {
        return auto;
    }

    public void setAuto(Boolean auto) {
        this.auto = auto;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
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
        if (!(o instanceof AssetDTO)) {
            return false;
        }

        AssetDTO assetDTO = (AssetDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, assetDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssetDTO{" +
            "id=" + getId() +
            ", version=" + getVersion() +
            ", name='" + getName() + "'" +
            ", main='" + getMain() + "'" +
            ", auto='" + getAuto() + "'" +
            ", path='" + getPath() + "'" +
            ", include='" + getInclude() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
