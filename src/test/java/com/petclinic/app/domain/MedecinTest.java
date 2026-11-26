package com.petclinic.app.domain;

import static com.petclinic.app.domain.CliniqueTestSamples.*;
import static com.petclinic.app.domain.MedecinTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.petclinic.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MedecinTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Medecin.class);
        Medecin medecin1 = getMedecinSample1();
        Medecin medecin2 = new Medecin();
        assertThat(medecin1).isNotEqualTo(medecin2);

        medecin2.setId(medecin1.getId());
        assertThat(medecin1).isEqualTo(medecin2);

        medecin2 = getMedecinSample2();
        assertThat(medecin1).isNotEqualTo(medecin2);
    }

    @Test
    void cliniqueTest() {
        Medecin medecin = getMedecinRandomSampleGenerator();
        Clinique cliniqueBack = getCliniqueRandomSampleGenerator();

        medecin.setClinique(cliniqueBack);
        assertThat(medecin.getClinique()).isEqualTo(cliniqueBack);

        medecin.clinique(null);
        assertThat(medecin.getClinique()).isNull();
    }
}
