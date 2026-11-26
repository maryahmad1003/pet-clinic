package com.petclinic.app.domain;

import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Animal.
 */
@Table("animal")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Animal implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column("nom")
    private String nom;

    @NotNull
    @Size(max = 50)
    @Column("espece")
    private String espece;

    @Column("date_naissance")
    private LocalDate dateNaissance;

    @DecimalMin(value = "0")
    @Column("poids")
    private BigDecimal poids;

    @org.springframework.data.annotation.Transient
    private Client client;

    @Column("client_id")
    private Long clientId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Animal id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Animal nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEspece() {
        return this.espece;
    }

    public Animal espece(String espece) {
        this.setEspece(espece);
        return this;
    }

    public void setEspece(String espece) {
        this.espece = espece;
    }

    public LocalDate getDateNaissance() {
        return this.dateNaissance;
    }

    public Animal dateNaissance(LocalDate dateNaissance) {
        this.setDateNaissance(dateNaissance);
        return this;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public BigDecimal getPoids() {
        return this.poids;
    }

    public Animal poids(BigDecimal poids) {
        this.setPoids(poids);
        return this;
    }

    public void setPoids(BigDecimal poids) {
        this.poids = poids;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
        this.clientId = client != null ? client.getId() : null;
    }

    public Animal client(Client client) {
        this.setClient(client);
        return this;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public void setClientId(Long client) {
        this.clientId = client;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Animal)) {
            return false;
        }
        return getId() != null && getId().equals(((Animal) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Animal{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", espece='" + getEspece() + "'" +
            ", dateNaissance='" + getDateNaissance() + "'" +
            ", poids=" + getPoids() +
            "}";
    }
}
