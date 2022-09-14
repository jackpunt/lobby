package com.thegraid.lobby.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Assets owned by a member/user; (the horses) a virtual file-system?
 */
@Entity
@Table(name = "asset")
public class Asset implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "version")
    private Integer version;

    /**
     * display name
     */
    @Size(max = 45)
    @Column(name = "name", length = 45)
    private String name;

    /**
     * IPlayer - can be player.main_jar
     */
    @Column(name = "main")
    private Boolean main;

    /**
     * bot is full-auto
     */
    @Column(name = "auto")
    private Boolean auto;

    /**
     * url to asset (class or document/resource)\njar-path ! fqcn.of.asset.class\njar-path ! path/inside/jar/document\nURL=getenv(“ASSETBASE”)+path/to/release.jar ! user.supplied.Player
     */
    @Column(name = "path")
    private String path;

    /**
     * comma-separated list of asset Ids
     */
    @Column(name = "include")
    private String include;

    @ManyToOne
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Asset id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public Asset version(Integer version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getName() {
        return this.name;
    }

    public Asset name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getMain() {
        return this.main;
    }

    public Asset main(Boolean main) {
        this.setMain(main);
        return this;
    }

    public void setMain(Boolean main) {
        this.main = main;
    }

    public Boolean getAuto() {
        return this.auto;
    }

    public Asset auto(Boolean auto) {
        this.setAuto(auto);
        return this;
    }

    public void setAuto(Boolean auto) {
        this.auto = auto;
    }

    public String getPath() {
        return this.path;
    }

    public Asset path(String path) {
        this.setPath(path);
        return this;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getInclude() {
        return this.include;
    }

    public Asset include(String include) {
        this.setInclude(include);
        return this;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Asset user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Asset)) {
            return false;
        }
        return id != null && id.equals(((Asset) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Asset{" +
            "id=" + getId() +
            ", version=" + getVersion() +
            ", name='" + getName() + "'" +
            ", main='" + getMain() + "'" +
            ", auto='" + getAuto() + "'" +
            ", path='" + getPath() + "'" +
            ", include='" + getInclude() + "'" +
            "}";
    }
}
