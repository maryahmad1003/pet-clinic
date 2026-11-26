package com.petclinic.app.repository;

import com.petclinic.app.domain.RendezVous;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the RendezVous entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RendezVousRepository extends ReactiveCrudRepository<RendezVous, Long>, RendezVousRepositoryInternal {
    Flux<RendezVous> findAllBy(Pageable pageable);

    @Override
    Mono<RendezVous> findOneWithEagerRelationships(Long id);

    @Override
    Flux<RendezVous> findAllWithEagerRelationships();

    @Override
    Flux<RendezVous> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM rendez_vous entity WHERE entity.animal_id = :id")
    Flux<RendezVous> findByAnimal(Long id);

    @Query("SELECT * FROM rendez_vous entity WHERE entity.animal_id IS NULL")
    Flux<RendezVous> findAllWhereAnimalIsNull();

    @Query("SELECT * FROM rendez_vous entity WHERE entity.medecin_id = :id")
    Flux<RendezVous> findByMedecin(Long id);

    @Query("SELECT * FROM rendez_vous entity WHERE entity.medecin_id IS NULL")
    Flux<RendezVous> findAllWhereMedecinIsNull();

    @Query("SELECT * FROM rendez_vous entity WHERE entity.clinique_id = :id")
    Flux<RendezVous> findByClinique(Long id);

    @Query("SELECT * FROM rendez_vous entity WHERE entity.clinique_id IS NULL")
    Flux<RendezVous> findAllWhereCliniqueIsNull();

    @Override
    <S extends RendezVous> Mono<S> save(S entity);

    @Override
    Flux<RendezVous> findAll();

    @Override
    Mono<RendezVous> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RendezVousRepositoryInternal {
    Flux<RendezVous> findAllBy(Pageable pageable);

    Flux<RendezVous> findAll();

    Mono<RendezVous> findById(Long id);

    Mono<RendezVous> findOneWithEagerRelationships(Long id);

    Flux<RendezVous> findAllWithEagerRelationships();

    Flux<RendezVous> findAllWithEagerRelationships(Pageable page);
}
