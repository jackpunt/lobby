package com.thegraid.lobby.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Member-owned PlayerAI [Asset] with a displayClient [Asset]\na virtual player (the horse in a horse-race)
 */
@Entity
@Table(name = "player")
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "version")
    private Integer version;

    /**
     * display name, as set by the owning Member.
     */
    @Size(max = 64)
    @Column(name = "name", length = 64)
    private String name;

    /**
     * NULL until ranked
     */
    @Column(name = "jhi_rank")
    private Integer rank;

    /**
     * initial 0
     */
    @Column(name = "score")
    private Integer score;

    @Column(name = "score_time")
    private Instant scoreTime;

    @Column(name = "rank_time")
    private Instant rankTime;

    /**
     * URL path fragment to download display client from graid server.\nProbably redo as reference to display_client table entry or an asset entry.
     */
    @Size(max = 64)
    @Column(name = "display_client", length = 64)
    private String displayClient;

    /**
     * what Games this Player can play
     */
    @ManyToOne
    private GameClass gameClass;

    /**
     * identify the code that implements this PlayerAI: a registered Asset.
     */
    @ManyToOne
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Asset mainJar;

    @ManyToOne
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Player id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public Player version(Integer version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getName() {
        return this.name;
    }

    public Player name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRank() {
        return this.rank;
    }

    public Player rank(Integer rank) {
        this.setRank(rank);
        return this;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getScore() {
        return this.score;
    }

    public Player score(Integer score) {
        this.setScore(score);
        return this;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Instant getScoreTime() {
        return this.scoreTime;
    }

    public Player scoreTime(Instant scoreTime) {
        this.setScoreTime(scoreTime);
        return this;
    }

    public void setScoreTime(Instant scoreTime) {
        this.scoreTime = scoreTime;
    }

    public Instant getRankTime() {
        return this.rankTime;
    }

    public Player rankTime(Instant rankTime) {
        this.setRankTime(rankTime);
        return this;
    }

    public void setRankTime(Instant rankTime) {
        this.rankTime = rankTime;
    }

    public String getDisplayClient() {
        return this.displayClient;
    }

    public Player displayClient(String displayClient) {
        this.setDisplayClient(displayClient);
        return this;
    }

    public void setDisplayClient(String displayClient) {
        this.displayClient = displayClient;
    }

    public GameClass getGameClass() {
        return this.gameClass;
    }

    public void setGameClass(GameClass gameClass) {
        this.gameClass = gameClass;
    }

    public Player gameClass(GameClass gameClass) {
        this.setGameClass(gameClass);
        return this;
    }

    public Asset getMainJar() {
        return this.mainJar;
    }

    public void setMainJar(Asset asset) {
        this.mainJar = asset;
    }

    public Player mainJar(Asset asset) {
        this.setMainJar(asset);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Player user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Player)) {
            return false;
        }
        return id != null && id.equals(((Player) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Player{" +
            "id=" + getId() +
            ", version=" + getVersion() +
            ", name='" + getName() + "'" +
            ", rank=" + getRank() +
            ", score=" + getScore() +
            ", scoreTime='" + getScoreTime() + "'" +
            ", rankTime='" + getRankTime() + "'" +
            ", displayClient='" + getDisplayClient() + "'" +
            "}";
    }
}
