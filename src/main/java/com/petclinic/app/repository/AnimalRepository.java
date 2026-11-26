package com.petclinic.app.repository;

import com.petclinic.app.domain.Animal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Animal entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnimalRepository extends ReactiveCrudRepository<Animal, Long>, AnimalRepositoryInternal {
    Flux<Animal> findAllBy(Pageable pageable);

    @Override
    Mono<Animal> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Animal> findAllWithEagerRelationships();

    @Override
    Flux<Animal> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM animal entity WHERE entity.client_id = :id")
    Flux<Animal> findByClient(Long id);

    @Query("SELECT * FROM animal entity WHERE entity.client_id IS NULL")
    Flux<Animal> findAllWhereClientIsNull();

    @Override
    <S extends Animal> Mono<S> save(S entity);

    @Override
    Flux<Animal> findAll();

    @Override
    Mono<Animal> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AnimalRepositoryInternal {
    Flux<Animal> findAllBy(Pageable pageable);

    Flux<Animal> findAll();

    Mono<Animal> findById(Long id);

    Mono<Animal> findOneWithEagerRelationships(Long id);

    Flux<Animal> findAllWithEagerRelationships();

    Flux<Animal> findAllWithEagerRelationships(Pageable page);
}
