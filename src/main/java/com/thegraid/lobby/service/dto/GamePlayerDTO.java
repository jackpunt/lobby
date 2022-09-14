package com.thegraid.lobby.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.thegraid.lobby.domain.GamePlayer} entity.
 */
@Schema(description = "one of 2 Players (A or B) in a GameInst")
public class GamePlayerDTO implements Serializable {

    private Long id;

    private Integer version;

    /**
     * assigned in Lobby
     */
    @NotNull
    @Size(max = 4)
    @Schema(description = "assigned in Lobby", required = true)
    private String role;

    /**
     * ack'd version; initial -1
     */
    @NotNull
    @Schema(description = "ack'd version; initial -1", required = true)
    private Integer ready;

    private GameInstDTO gameInst;

    private PlayerDTO player;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getReady() {
        return ready;
    }

    public void setReady(Integer ready) {
        this.ready = ready;
    }

    public GameInstDTO getGameInst() {
        return gameInst;
    }

    public void setGameInst(GameInstDTO gameInst) {
        this.gameInst = gameInst;
    }

    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GamePlayerDTO)) {
            return false;
        }

        GamePlayerDTO gamePlayerDTO = (GamePlayerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, gamePlayerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GamePlayerDTO{" +
            "id=" + getId() +
            ", version=" + getVersion() +
            ", role='" + getRole() + "'" +
            ", ready=" + getReady() +
            ", gameInst=" + getGameInst() +
            ", player=" + getPlayer() +
            "}";
    }
}
