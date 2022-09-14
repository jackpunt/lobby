package com.thegraid.lobby.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * member/player proposes properties for a new GameInst.
 */
@Entity
@Table(name = "member_game_props")
public class MemberGameProps implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    /**
     * MyGameConfig
     */
    @Size(max = 45)
    @Column(name = "config_name", length = 45)
    private String configName;

    @ManyToOne
    private User user;

    @ManyToOne
    private GameClass gameClass;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MemberGameProps id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public MemberGameProps version(Integer version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getSeed() {
        return this.seed;
    }

    public MemberGameProps seed(Long seed) {
        this.setSeed(seed);
        return this;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public String getMapName() {
        return this.mapName;
    }

    public MemberGameProps mapName(String mapName) {
        this.setMapName(mapName);
        return this;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public Integer getMapSize() {
        return this.mapSize;
    }

    public MemberGameProps mapSize(Integer mapSize) {
        this.setMapSize(mapSize);
        return this;
    }

    public void setMapSize(Integer mapSize) {
        this.mapSize = mapSize;
    }

    public Integer getNpcCount() {
        return this.npcCount;
    }

    public MemberGameProps npcCount(Integer npcCount) {
        this.setNpcCount(npcCount);
        return this;
    }

    public void setNpcCount(Integer npcCount) {
        this.npcCount = npcCount;
    }

    public String getJsonProps() {
        return this.jsonProps;
    }

    public MemberGameProps jsonProps(String jsonProps) {
        this.setJsonProps(jsonProps);
        return this;
    }

    public void setJsonProps(String jsonProps) {
        this.jsonProps = jsonProps;
    }

    public String getConfigName() {
        return this.configName;
    }

    public MemberGameProps configName(String configName) {
        this.setConfigName(configName);
        return this;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MemberGameProps user(User user) {
        this.setUser(user);
        return this;
    }

    public GameClass getGameClass() {
        return this.gameClass;
    }

    public void setGameClass(GameClass gameClass) {
        this.gameClass = gameClass;
    }

    public MemberGameProps gameClass(GameClass gameClass) {
        this.setGameClass(gameClass);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MemberGameProps)) {
            return false;
        }
        return id != null && id.equals(((MemberGameProps) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MemberGameProps{" +
            "id=" + getId() +
            ", version=" + getVersion() +
            ", seed=" + getSeed() +
            ", mapName='" + getMapName() + "'" +
            ", mapSize=" + getMapSize() +
            ", npcCount=" + getNpcCount() +
            ", jsonProps='" + getJsonProps() + "'" +
            ", configName='" + getConfigName() + "'" +
            "}";
    }
}
