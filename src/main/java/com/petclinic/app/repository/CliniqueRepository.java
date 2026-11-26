package com.petclinic.app.repository;

import com.petclinic.app.domain.Clinique;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data R2DBC repository for the Clinique entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CliniqueRepository extends R2dbcRepository<Clinique, Long> {
    Flux<Clinique> findAllBy(Pageable pageable);
}
