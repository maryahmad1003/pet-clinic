package com.petclinic.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RendezVousTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static RendezVous getRendezVousSample1() {
        return new RendezVous().id(1L).heure("heure1").motif("motif1");
    }

    public static RendezVous getRendezVousSample2() {
        return new RendezVous().id(2L).heure("heure2").motif("motif2");
    }

    public static RendezVous getRendezVousRandomSampleGenerator() {
        return new RendezVous().id(longCount.incrementAndGet()).heure(UUID.randomUUID().toString()).motif(UUID.randomUUID().toString());
    }
}
