package com.petclinic.app.domain;

import static com.petclinic.app.domain.CliniqueTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.petclinic.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CliniqueTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Clinique.class);
        Clinique clinique1 = getCliniqueSample1();
        Clinique clinique2 = new Clinique();
        assertThat(clinique1).isNotEqualTo(clinique2);

        clinique2.setId(clinique1.getId());
        assertThat(clinique1).isEqualTo(clinique2);

        clinique2 = getCliniqueSample2();
        assertThat(clinique1).isNotEqualTo(clinique2);
    }
}
