package com.petclinic.app.service;

import com.petclinic.app.domain.Client;
import com.petclinic.app.repository.ClientRepository;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.petclinic.app.domain.Client}.
 */
@Service
@Transactional
public class ClientService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Save a client.
     *
     * @param client the entity to save.
     * @return the persisted entity.
     */
    public Mono<Client> save(Client client) {
        LOG.debug("Request to save Client : {}", client);
        return clientRepository.save(client);
    }

    /**
     * Update a client.
     *
     * @param client the entity to save.
     * @return the persisted entity.
     */
    public Mono<Client> update(Client client) {
        LOG.debug("Request to update Client : {}", client);
        return clientRepository.save(client);
    }

    /**
     * Partially update a client.
     *
     * @param client the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Client> partialUpdate(Client client) {
        LOG.debug("Request to partially update Client : {}", client);

        return clientRepository
            .findById(client.getId())
            .map(existingClient -> {
                updateIfPresent(existingClient::setNom, client.getNom());
                updateIfPresent(existingClient::setPrenom, client.getPrenom());
                updateIfPresent(existingClient::setAdresse, client.getAdresse());
                updateIfPresent(existingClient::setTelephone, client.getTelephone());

                return existingClient;
            })
            .flatMap(clientRepository::save);
    }

    /**
     * Get all the clients.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Client> findAll(Pageable pageable) {
        LOG.debug("Request to get all Clients");
        return clientRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of clients available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return clientRepository.count();
    }

    /**
     * Get one client by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Client> findOne(Long id) {
        LOG.debug("Request to get Client : {}", id);
        return clientRepository.findById(id);
    }

    /**
     * Delete the client by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Client : {}", id);
        return clientRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
