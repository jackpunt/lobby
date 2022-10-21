package com.thegraid.lobby.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A DTO for the {@link com.thegraid.lobby.domain.GameInst} entity.
 */
@Schema(description = "Instance of a Game.")
public class GameInstDTO implements Serializable {

    private Long id;

    private Integer version;

    @Size(max = 64)
    private String gameName;

    @Size(max = 64)
    private String hostUrl;

    @Size(max = 64)
    private String passcode;

    @NotNull
    private Instant created;

    private Instant started;

    private Instant finished;

    @NotNull
    private Instant updated;

    private Integer scoreA;

    private Integer scoreB;

    private Integer ticks;

    private PlayerDTO playerA;

    private PlayerDTO playerB;

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

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getStarted() {
        return started;
    }

    public void setStarted(Instant started) {
        this.started = started;
    }

    public Instant getFinished() {
        return finished;
    }

    public void setFinished(Instant finished) {
        this.finished = finished;
    }

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public Integer getScoreA() {
        return scoreA;
    }

    public void setScoreA(Integer scoreA) {
        this.scoreA = scoreA;
    }

    public Integer getScoreB() {
        return scoreB;
    }

    public void setScoreB(Integer scoreB) {
        this.scoreB = scoreB;
    }

    public Integer getTicks() {
        return ticks;
    }

    public void setTicks(Integer ticks) {
        this.ticks = ticks;
    }

    public PlayerDTO getPlayerA() {
        return playerA;
    }

    public void setPlayerA(PlayerDTO playerA) {
        this.playerA = playerA;
    }

    public PlayerDTO getPlayerB() {
        return playerB;
    }

    public void setPlayerB(PlayerDTO playerB) {
        this.playerB = playerB;
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
        if (!(o instanceof GameInstDTO)) {
            return false;
        }

        GameInstDTO gameInstDTO = (GameInstDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, gameInstDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GameInstDTO{" +
            "id=" + getId() +
            ", version=" + getVersion() +
            ", gameName='" + getGameName() + "'" +
            ", hostUrl='" + getHostUrl() + "'" +
            ", passcode='" + getPasscode() + "'" +
            ", created='" + getCreated() + "'" +
            ", started='" + getStarted() + "'" +
            ", finished='" + getFinished() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", scoreA=" + getScoreA() +
            ", scoreB=" + getScoreB() +
            ", ticks=" + getTicks() +
            ", playerA=" + getPlayerA() +
            ", playerB=" + getPlayerB() +
            ", gameClass=" + getGameClass() +
            "}";
    }
}
