package com.petclinic.app.repository;

import com.petclinic.app.domain.Clinique;
import com.petclinic.app.domain.Medecin;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Medecin entity.
 */
@SuppressWarnings("unused")
class MedecinRepositoryInternalImpl extends AbstractR2dbcRepository<Medecin, Long> implements MedecinRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM medecin e" + " LEFT JOIN clinique clinique ON e.clinique_id = clinique.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("clinique.id", "clinique.id"),
        Map.entry("clinique.nom", "clinique.nom")
    );

    public MedecinRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(Medecin.class, template, converter, dialect);
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
    protected Mono<List<Medecin>> populateRelationships(List<Medecin> entities) {
        return Mono.just(entities).flatMap(populated ->
            populate(populated, Medecin::getCliniqueId, Clinique.class, Clinique::getId, Medecin::setClinique)
        );
    }
}
