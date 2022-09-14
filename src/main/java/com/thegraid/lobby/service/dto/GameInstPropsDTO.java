package com.thegraid.lobby.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.thegraid.lobby.domain.GameInstProps} entity.
 */
@Schema(description = "the final negotiated properties for GameInst")
public class GameInstPropsDTO implements Serializable {

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

    @NotNull
    private Instant updated;

    private GameInstDTO gameInst;

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

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public GameInstDTO getGameInst() {
        return gameInst;
    }

    public void setGameInst(GameInstDTO gameInst) {
        this.gameInst = gameInst;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GameInstPropsDTO)) {
            return false;
        }

        GameInstPropsDTO gameInstPropsDTO = (GameInstPropsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, gameInstPropsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GameInstPropsDTO{" +
            "id=" + getId() +
            ", version=" + getVersion() +
            ", seed=" + getSeed() +
            ", mapName='" + getMapName() + "'" +
            ", mapSize=" + getMapSize() +
            ", npcCount=" + getNpcCount() +
            ", jsonProps='" + getJsonProps() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", gameInst=" + getGameInst() +
            "}";
    }
}
