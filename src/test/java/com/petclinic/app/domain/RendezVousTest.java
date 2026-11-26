package com.petclinic.app.domain;

import static com.petclinic.app.domain.AnimalTestSamples.*;
import static com.petclinic.app.domain.CliniqueTestSamples.*;
import static com.petclinic.app.domain.MedecinTestSamples.*;
import static com.petclinic.app.domain.RendezVousTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.petclinic.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RendezVousTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RendezVous.class);
        RendezVous rendezVous1 = getRendezVousSample1();
        RendezVous rendezVous2 = new RendezVous();
        assertThat(rendezVous1).isNotEqualTo(rendezVous2);

        rendezVous2.setId(rendezVous1.getId());
        assertThat(rendezVous1).isEqualTo(rendezVous2);

        rendezVous2 = getRendezVousSample2();
        assertThat(rendezVous1).isNotEqualTo(rendezVous2);
    }

    @Test
    void animalTest() {
        RendezVous rendezVous = getRendezVousRandomSampleGenerator();
        Animal animalBack = getAnimalRandomSampleGenerator();

        rendezVous.setAnimal(animalBack);
        assertThat(rendezVous.getAnimal()).isEqualTo(animalBack);

        rendezVous.animal(null);
        assertThat(rendezVous.getAnimal()).isNull();
    }

    @Test
    void medecinTest() {
        RendezVous rendezVous = getRendezVousRandomSampleGenerator();
        Medecin medecinBack = getMedecinRandomSampleGenerator();

        rendezVous.setMedecin(medecinBack);
        assertThat(rendezVous.getMedecin()).isEqualTo(medecinBack);

        rendezVous.medecin(null);
        assertThat(rendezVous.getMedecin()).isNull();
    }

    @Test
    void cliniqueTest() {
        RendezVous rendezVous = getRendezVousRandomSampleGenerator();
        Clinique cliniqueBack = getCliniqueRandomSampleGenerator();

        rendezVous.setClinique(cliniqueBack);
        assertThat(rendezVous.getClinique()).isEqualTo(cliniqueBack);

        rendezVous.clinique(null);
        assertThat(rendezVous.getClinique()).isNull();
    }
}
