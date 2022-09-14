package com.thegraid.lobby.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.thegraid.lobby.domain.GameClass} entity.
 */
@Schema(description = "Which Game engine/jar to play.")
public class GameClassDTO implements Serializable {

    private Long id;

    private Integer version;

    /**
     * a unique name for this GameClass
     */
    @NotNull
    @Size(max = 45)
    @Schema(description = "a unique name for this GameClass", required = true)
    private String name;

    /**
     * major.minor.patch.TYPE [semver]
     */
    @Size(max = 24)
    @Schema(description = "major.minor.patch.TYPE [semver]")
    private String revision;

    /**
     * jar-path ! fqcn.of.launcher\nURL=getenv(“GAMEBASE”)+path/to/release.jar ! fqcn.launcher
     */
    @Schema(description = "jar-path ! fqcn.of.launcher\nURL=getenv(“GAMEBASE”)+path/to/release.jar ! fqcn.launcher")
    private String launcherPath;

    /**
     * jar-path ! fqcn.of.game\nURL=getenv(“GAMEBASE”)+path/to/release.jar ! pkg.main
     */
    @Schema(description = "jar-path ! fqcn.of.game\nURL=getenv(“GAMEBASE”)+path/to/release.jar ! pkg.main")
    private String gamePath;

    /**
     * doc-path/to/index.html\nURL=getenv(“GAMEBASE”)+path/to/release.jar ! doc/path/index.html
     */
    @Schema(description = "doc-path/to/index.html\nURL=getenv(“GAMEBASE”)+path/to/release.jar ! doc/path/index.html")
    private String docsPath;

    /**
     * a comma-separated string of property names for this GameClass\nonly these prop_names can appear in the game_props.json associated with this game_class
     */
    @Schema(
        description = "a comma-separated string of property names for this GameClass\nonly these prop_names can appear in the game_props.json associated with this game_class"
    )
    private String propNames;

    @NotNull
    private Instant updated;

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

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getLauncherPath() {
        return launcherPath;
    }

    public void setLauncherPath(String launcherPath) {
        this.launcherPath = launcherPath;
    }

    public String getGamePath() {
        return gamePath;
    }

    public void setGamePath(String gamePath) {
        this.gamePath = gamePath;
    }

    public String getDocsPath() {
        return docsPath;
    }

    public void setDocsPath(String docsPath) {
        this.docsPath = docsPath;
    }

    public String getPropNames() {
        return propNames;
    }

    public void setPropNames(String propNames) {
        this.propNames = propNames;
    }

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GameClassDTO)) {
            return false;
        }

        GameClassDTO gameClassDTO = (GameClassDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, gameClassDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GameClassDTO{" +
            "id=" + getId() +
            ", version=" + getVersion() +
            ", name='" + getName() + "'" +
            ", revision='" + getRevision() + "'" +
            ", launcherPath='" + getLauncherPath() + "'" +
            ", gamePath='" + getGamePath() + "'" +
            ", docsPath='" + getDocsPath() + "'" +
            ", propNames='" + getPropNames() + "'" +
            ", updated='" + getUpdated() + "'" +
            "}";
    }
}
