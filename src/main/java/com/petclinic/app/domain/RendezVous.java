package com.petclinic.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A RendezVous.
 */
@Table("rendez_vous")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RendezVous implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("date")
    private LocalDate date;

    @NotNull
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$")
    @Column("heure")
    private String heure;

    @NotNull
    @Size(max = 500)
    @Column("motif")
    private String motif;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "client" }, allowSetters = true)
    private Animal animal;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "clinique" }, allowSetters = true)
    private Medecin medecin;

    @org.springframework.data.annotation.Transient
    private Clinique clinique;

    @Column("animal_id")
    private Long animalId;

    @Column("medecin_id")
    private Long medecinId;

    @Column("clinique_id")
    private Long cliniqueId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RendezVous id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public RendezVous date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getHeure() {
        return this.heure;
    }

    public RendezVous heure(String heure) {
        this.setHeure(heure);
        return this;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getMotif() {
        return this.motif;
    }

    public RendezVous motif(String motif) {
        this.setMotif(motif);
        return this;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public Animal getAnimal() {
        return this.animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
        this.animalId = animal != null ? animal.getId() : null;
    }

    public RendezVous animal(Animal animal) {
        this.setAnimal(animal);
        return this;
    }

    public Medecin getMedecin() {
        return this.medecin;
    }

    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
        this.medecinId = medecin != null ? medecin.getId() : null;
    }

    public RendezVous medecin(Medecin medecin) {
        this.setMedecin(medecin);
        return this;
    }

    public Clinique getClinique() {
        return this.clinique;
    }

    public void setClinique(Clinique clinique) {
        this.clinique = clinique;
        this.cliniqueId = clinique != null ? clinique.getId() : null;
    }

    public RendezVous clinique(Clinique clinique) {
        this.setClinique(clinique);
        return this;
    }

    public Long getAnimalId() {
        return this.animalId;
    }

    public void setAnimalId(Long animal) {
        this.animalId = animal;
    }

    public Long getMedecinId() {
        return this.medecinId;
    }

    public void setMedecinId(Long medecin) {
        this.medecinId = medecin;
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
        if (!(o instanceof RendezVous)) {
            return false;
        }
        return getId() != null && getId().equals(((RendezVous) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RendezVous{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", heure='" + getHeure() + "'" +
            ", motif='" + getMotif() + "'" +
            "}";
    }
}
