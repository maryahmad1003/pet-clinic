package com.petclinic.app.repository;

import com.petclinic.app.domain.Medecin;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Medecin entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MedecinRepository extends ReactiveCrudRepository<Medecin, Long>, MedecinRepositoryInternal {
    Flux<Medecin> findAllBy(Pageable pageable);

    @Override
    Mono<Medecin> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Medecin> findAllWithEagerRelationships();

    @Override
    Flux<Medecin> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM medecin entity WHERE entity.clinique_id = :id")
    Flux<Medecin> findByClinique(Long id);

    @Query("SELECT * FROM medecin entity WHERE entity.clinique_id IS NULL")
    Flux<Medecin> findAllWhereCliniqueIsNull();

    @Override
    <S extends Medecin> Mono<S> save(S entity);

    @Override
    Flux<Medecin> findAll();

    @Override
    Mono<Medecin> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MedecinRepositoryInternal {
    Flux<Medecin> findAllBy(Pageable pageable);

    Flux<Medecin> findAll();

    Mono<Medecin> findById(Long id);

    Mono<Medecin> findOneWithEagerRelationships(Long id);

    Flux<Medecin> findAllWithEagerRelationships();

    Flux<Medecin> findAllWithEagerRelationships(Pageable page);
}
