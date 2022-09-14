package com.thegraid.lobby.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Which Game engine/jar to play.
 */
@Entity
@Table(name = "game_class")
public class GameClass implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "version")
    private Integer version;

    /**
     * a unique name for this GameClass
     */
    @NotNull
    @Size(max = 45)
    @Column(name = "name", length = 45, nullable = false)
    private String name;

    /**
     * major.minor.patch.TYPE [semver]
     */
    @Size(max = 24)
    @Column(name = "revision", length = 24)
    private String revision;

    /**
     * jar-path ! fqcn.of.launcher\nURL=getenv(“GAMEBASE”)+path/to/release.jar ! fqcn.launcher
     */
    @Column(name = "launcher_path")
    private String launcherPath;

    /**
     * jar-path ! fqcn.of.game\nURL=getenv(“GAMEBASE”)+path/to/release.jar ! pkg.main
     */
    @Column(name = "game_path")
    private String gamePath;

    /**
     * doc-path/to/index.html\nURL=getenv(“GAMEBASE”)+path/to/release.jar ! doc/path/index.html
     */
    @Column(name = "docs_path")
    private String docsPath;

    /**
     * a comma-separated string of property names for this GameClass\nonly these prop_names can appear in the game_props.json associated with this game_class
     */
    @Column(name = "prop_names")
    private String propNames;

    @NotNull
    @Column(name = "updated", nullable = false)
    private Instant updated;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GameClass id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public GameClass version(Integer version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getName() {
        return this.name;
    }

    public GameClass name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRevision() {
        return this.revision;
    }

    public GameClass revision(String revision) {
        this.setRevision(revision);
        return this;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getLauncherPath() {
        return this.launcherPath;
    }

    public GameClass launcherPath(String launcherPath) {
        this.setLauncherPath(launcherPath);
        return this;
    }

    public void setLauncherPath(String launcherPath) {
        this.launcherPath = launcherPath;
    }

    public String getGamePath() {
        return this.gamePath;
    }

    public GameClass gamePath(String gamePath) {
        this.setGamePath(gamePath);
        return this;
    }

    public void setGamePath(String gamePath) {
        this.gamePath = gamePath;
    }

    public String getDocsPath() {
        return this.docsPath;
    }

    public GameClass docsPath(String docsPath) {
        this.setDocsPath(docsPath);
        return this;
    }

    public void setDocsPath(String docsPath) {
        this.docsPath = docsPath;
    }

    public String getPropNames() {
        return this.propNames;
    }

    public GameClass propNames(String propNames) {
        this.setPropNames(propNames);
        return this;
    }

    public void setPropNames(String propNames) {
        this.propNames = propNames;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public GameClass updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GameClass)) {
            return false;
        }
        return id != null && id.equals(((GameClass) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GameClass{" +
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
