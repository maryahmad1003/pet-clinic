package com.petclinic.app.service;

import com.petclinic.app.domain.RendezVous;
import com.petclinic.app.repository.RendezVousRepository;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.petclinic.app.domain.RendezVous}.
 */
@Service
@Transactional
public class RendezVousService {

    private static final Logger LOG = LoggerFactory.getLogger(RendezVousService.class);

    private final RendezVousRepository rendezVousRepository;

    public RendezVousService(RendezVousRepository rendezVousRepository) {
        this.rendezVousRepository = rendezVousRepository;
    }

    /**
     * Save a rendezVous.
     *
     * @param rendezVous the entity to save.
     * @return the persisted entity.
     */
    public Mono<RendezVous> save(RendezVous rendezVous) {
        LOG.debug("Request to save RendezVous : {}", rendezVous);
        return rendezVousRepository.save(rendezVous);
    }

    /**
     * Update a rendezVous.
     *
     * @param rendezVous the entity to save.
     * @return the persisted entity.
     */
    public Mono<RendezVous> update(RendezVous rendezVous) {
        LOG.debug("Request to update RendezVous : {}", rendezVous);
        return rendezVousRepository.save(rendezVous);
    }

    /**
     * Partially update a rendezVous.
     *
     * @param rendezVous the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<RendezVous> partialUpdate(RendezVous rendezVous) {
        LOG.debug("Request to partially update RendezVous : {}", rendezVous);

        return rendezVousRepository
            .findById(rendezVous.getId())
            .map(existingRendezVous -> {
                updateIfPresent(existingRendezVous::setDate, rendezVous.getDate());
                updateIfPresent(existingRendezVous::setHeure, rendezVous.getHeure());
                updateIfPresent(existingRendezVous::setMotif, rendezVous.getMotif());

                return existingRendezVous;
            })
            .flatMap(rendezVousRepository::save);
    }

    /**
     * Get all the rendezVouses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RendezVous> findAll(Pageable pageable) {
        LOG.debug("Request to get all RendezVouses");
        return rendezVousRepository.findAllBy(pageable);
    }

    /**
     * Get all the rendezVouses with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<RendezVous> findAllWithEagerRelationships(Pageable pageable) {
        return rendezVousRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Returns the number of rendezVouses available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return rendezVousRepository.count();
    }

    /**
     * Get one rendezVous by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<RendezVous> findOne(Long id) {
        LOG.debug("Request to get RendezVous : {}", id);
        return rendezVousRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the rendezVous by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete RendezVous : {}", id);
        return rendezVousRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
