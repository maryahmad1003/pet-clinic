package com.petclinic.app.domain;

import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Medecin.
 */
@Table("medecin")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Medecin implements Serializable {

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
    @Size(max = 80)
    @Column("prenom")
    private String prenom;

    @NotNull
    @Size(max = 100)
    @Column("specialite")
    private String specialite;

    @NotNull
    @Column("email")
    private String email;

    @org.springframework.data.annotation.Transient
    private Clinique clinique;

    @Column("clinique_id")
    private Long cliniqueId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Medecin id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Medecin nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public Medecin prenom(String prenom) {
        this.setPrenom(prenom);
        return this;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getSpecialite() {
        return this.specialite;
    }

    public Medecin specialite(String specialite) {
        this.setSpecialite(specialite);
        return this;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getEmail() {
        return this.email;
    }

    public Medecin email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Clinique getClinique() {
        return this.clinique;
    }

    public void setClinique(Clinique clinique) {
        this.clinique = clinique;
        this.cliniqueId = clinique != null ? clinique.getId() : null;
    }

    public Medecin clinique(Clinique clinique) {
        this.setClinique(clinique);
        return this;
    }

    public Long getCliniqueId() {
        return this.cliniqueId;
    }

    public void setCliniqueId(Long clinique) {
        this.cliniqueId = clinique;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Medecin)) {
            return false;
        }
        return getId() != null && getId().equals(((Medecin) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Medecin{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", specialite='" + getSpecialite() + "'" +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
