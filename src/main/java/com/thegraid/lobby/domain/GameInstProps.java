package com.thegraid.lobby.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * the final negotiated properties for GameInst
 */
@Entity
@Table(name = "game_inst_props")
public class GameInstProps implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "version")
    private Integer version;

    @Column(name = "seed")
    private Long seed;

    /**
     * NULL means use normal/standard
     */
    @Size(max = 45)
    @Column(name = "map_name", length = 45)
    private String mapName;

    /**
     * NULL means not-specified
     */
    @Column(name = "map_size")
    private Integer mapSize;

    /**
     * NULL means not-specified
     */
    @Column(name = "npc_count")
    private Integer npcCount;

    /**
     * json form of game-specific properties
     */
    @Column(name = "json_props")
    private String jsonProps;

    @NotNull
    @Column(name = "updated", nullable = false)
    private Instant updated;

    @JsonIgnoreProperties(value = { "playerA", "playerB", "gameClass", "props" }, allowSetters = true)
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private GameInst gameInst;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GameInstProps id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public GameInstProps version(Integer version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getSeed() {
        return this.seed;
    }

    public GameInstProps seed(Long seed) {
        this.setSeed(seed);
        return this;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public String getMapName() {
        return this.mapName;
    }

    public GameInstProps mapName(String mapName) {
        this.setMapName(mapName);
        return this;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public Integer getMapSize() {
        return this.mapSize;
    }

    public GameInstProps mapSize(Integer mapSize) {
        this.setMapSize(mapSize);
        return this;
    }

    public void setMapSize(Integer mapSize) {
        this.mapSize = mapSize;
    }

    public Integer getNpcCount() {
        return this.npcCount;
    }

    public GameInstProps npcCount(Integer npcCount) {
        this.setNpcCount(npcCount);
        return this;
    }

    public void setNpcCount(Integer npcCount) {
        this.npcCount = npcCount;
    }

    public String getJsonProps() {
        return this.jsonProps;
    }

    public GameInstProps jsonProps(String jsonProps) {
        this.setJsonProps(jsonProps);
        return this;
    }

    public void setJsonProps(String jsonProps) {
        this.jsonProps = jsonProps;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public GameInstProps updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public GameInst getGameInst() {
        return this.gameInst;
    }

    public void setGameInst(GameInst gameInst) {
        this.gameInst = gameInst;
    }

    public GameInstProps gameInst(GameInst gameInst) {
        this.setGameInst(gameInst);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GameInstProps)) {
            return false;
        }
        return id != null && id.equals(((GameInstProps) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GameInstProps{" +
            "id=" + getId() +
            ", version=" + getVersion() +
            ", seed=" + getSeed() +
            ", mapName='" + getMapName() + "'" +
            ", mapSize=" + getMapSize() +
            ", npcCount=" + getNpcCount() +
            ", jsonProps='" + getJsonProps() + "'" +
            ", updated='" + getUpdated() + "'" +
            "}";
    }
}
