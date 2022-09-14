package com.thegraid.lobby.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * extension to User (user is the owner of a stable of horses)\nAccount Type indicates the payment properties and the League user is in.\nmaybe this could fold into User.role ?
 */
@Entity
@Table(name = "account_info")
public class AccountInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "version")
    private Integer version;

    @Column(name = "type")
    private String type;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AccountInfo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public AccountInfo version(Integer version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getType() {
        return this.type;
    }

    public AccountInfo type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AccountInfo user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccountInfo)) {
            return false;
        }
        return id != null && id.equals(((AccountInfo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AccountInfo{" +
            "id=" + getId() +
            ", version=" + getVersion() +
            ", type='" + getType() + "'" +
            "}";
    }
}
