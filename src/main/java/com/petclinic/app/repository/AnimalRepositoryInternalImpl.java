package com.petclinic.app.repository;

import com.petclinic.app.domain.Animal;
import com.petclinic.app.domain.Client;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Animal entity.
 */
@SuppressWarnings("unused")
class AnimalRepositoryInternalImpl extends AbstractR2dbcRepository<Animal, Long> implements AnimalRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM animal e" + " LEFT JOIN client client ON e.client_id = client.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("client.id", "client.id"),
        Map.entry("client.nom", "client.nom")
    );

    public AnimalRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(Animal.class, template, converter, dialect);
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
    protected Mono<List<Animal>> populateRelationships(List<Animal> entities) {
        return Mono.just(entities).flatMap(populated ->
            populate(populated, Animal::getClientId, Client.class, Client::getId, Animal::setClient)
        );
    }
}
