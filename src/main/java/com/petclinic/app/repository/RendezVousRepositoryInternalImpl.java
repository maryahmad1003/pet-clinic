package com.petclinic.app.repository;

import com.petclinic.app.domain.Animal;
import com.petclinic.app.domain.Clinique;
import com.petclinic.app.domain.Medecin;
import com.petclinic.app.domain.RendezVous;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the RendezVous entity.
 */
@SuppressWarnings("unused")
class RendezVousRepositoryInternalImpl extends AbstractR2dbcRepository<RendezVous, Long> implements RendezVousRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM rendez_vous e" +
        " LEFT JOIN animal animal ON e.animal_id = animal.id" +
        " LEFT JOIN medecin medecin ON e.medecin_id = medecin.id" +
        " LEFT JOIN clinique clinique ON e.clinique_id = clinique.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("animal.id", "animal.id"),
        Map.entry("animal.nom", "animal.nom"),
        Map.entry("medecin.id", "medecin.id"),
        Map.entry("medecin.nom", "medecin.nom"),
        Map.entry("clinique.id", "clinique.id"),
        Map.entry("clinique.nom", "clinique.nom")
    );

    public RendezVousRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(RendezVous.class, template, converter, dialect);
    }

    @Override
    protected String selectWithRelationsSql() {
        return SELECT_WITH_RELATIONS_SQL;
    }

    @Override
    protected Map<String, String> sortColumns() {
        return SORT_COLUMNS;
    }

    @Override
    protected Mono<List<RendezVous>> populateRelationships(List<RendezVous> entities) {
        return Mono.just(entities)
            .flatMap(populated -> populate(populated, RendezVous::getAnimalId, Animal.class, Animal::getId, RendezVous::setAnimal))
            .flatMap(populated -> populate(populated, RendezVous::getMedecinId, Medecin.class, Medecin::getId, RendezVous::setMedecin))
            .flatMap(populated -> populate(populated, RendezVous::getCliniqueId, Clinique.class, Clinique::getId, RendezVous::setClinique));
    }
}
