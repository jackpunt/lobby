package com.thegraid.lobby.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.thegraid.lobby.domain.MemberGameProps} entity.
 */
@Schema(description = "member/player proposes properties for a new GameInst.")
public class MemberGamePropsDTO implements Serializable {

    private Long id;

    private Integer version;

    private Long seed;

    /**
     * NULL means use normal/standard
     */
    @Size(max = 45)
    @Schema(description = "NULL means use normal/standard")
    private String mapName;

    /**
     * NULL means not-specified
     */
    @Schema(description = "NULL means not-specified")
    private Integer mapSize;

    /**
     * NULL means not-specified
     */
    @Schema(description = "NULL means not-specified")
    private Integer npcCount;

    /**
     * json form of game-specific properties
     */
    @Schema(description = "json form of game-specific properties")
    private String jsonProps;

    /**
     * MyGameConfig
     */
    @Size(max = 45)
    @Schema(description = "MyGameConfig")
    private String configName;

    private UserDTO user;

    private GameClassDTO gameClass;

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

    public Long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public Integer getMapSize() {
        return mapSize;
    }

    public void setMapSize(Integer mapSize) {
        this.mapSize = mapSize;
    }

    public Integer getNpcCount() {
        return npcCount;
    }

    public void setNpcCount(Integer npcCount) {
        this.npcCount = npcCount;
    }

    public String getJsonProps() {
        return jsonProps;
    }

    public void setJsonProps(String jsonProps) {
        this.jsonProps = jsonProps;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public GameClassDTO getGameClass() {
        return gameClass;
    }

    public void setGameClass(GameClassDTO gameClass) {
        this.gameClass = gameClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MemberGamePropsDTO)) {
            return false;
        }

        MemberGamePropsDTO memberGamePropsDTO = (MemberGamePropsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, memberGamePropsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MemberGamePropsDTO{" +
            "id=" + getId() +
            ", version=" + getVersion() +
            ", seed=" + getSeed() +
            ", mapName='" + getMapName() + "'" +
            ", mapSize=" + getMapSize() +
            ", npcCount=" + getNpcCount() +
            ", jsonProps='" + getJsonProps() + "'" +
            ", configName='" + getConfigName() + "'" +
            ", user=" + getUser() +
            ", gameClass=" + getGameClass() +
            "}";
    }
}
