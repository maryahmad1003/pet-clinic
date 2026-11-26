package com.petclinic.app.repository;

import com.petclinic.app.domain.Client;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data R2DBC repository for the Client entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientRepository extends R2dbcRepository<Client, Long> {
    Flux<Client> findAllBy(Pageable pageable);
}
