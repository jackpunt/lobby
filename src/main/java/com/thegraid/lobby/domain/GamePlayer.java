package com.thegraid.lobby.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * one of 2 Players (A or B) in a GameInst
 */
@Entity
@Table(name = "game_player")
public class GamePlayer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "version")
    private Integer version;

    /**
     * assigned in Lobby
     */
    @NotNull
    @Size(max = 4)
    @Column(name = "role", length = 4, nullable = false)
    private String role;

    /**
     * ack'd version; initial -1
     */
    @NotNull
    @Column(name = "ready", nullable = false)
    private Integer ready;

    @ManyToOne
    @JsonIgnoreProperties(value = { "playerA", "playerB", "gameClass", "props" }, allowSetters = true)
    private GameInst gameInst;

    @ManyToOne
    @JsonIgnoreProperties(value = { "gameClass", "mainJar", "user" }, allowSetters = true)
    private Player player;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GamePlayer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public GamePlayer version(Integer version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getRole() {
        return this.role;
    }

    public GamePlayer role(String role) {
        this.setRole(role);
        return this;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getReady() {
        return this.ready;
    }

    public GamePlayer ready(Integer ready) {
        this.setReady(ready);
        return this;
    }

    public void setReady(Integer ready) {
        this.ready = ready;
    }

    public GameInst getGameInst() {
        return this.gameInst;
    }

    public void setGameInst(GameInst gameInst) {
        this.gameInst = gameInst;
    }

    public GamePlayer gameInst(GameInst gameInst) {
        this.setGameInst(gameInst);
        return this;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public GamePlayer player(Player player) {
        this.setPlayer(player);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GamePlayer)) {
            return false;
        }
        return id != null && id.equals(((GamePlayer) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GamePlayer{" +
            "id=" + getId() +
            ", version=" + getVersion() +
            ", role='" + getRole() + "'" +
            ", ready=" + getReady() +
            "}";
    }
}
