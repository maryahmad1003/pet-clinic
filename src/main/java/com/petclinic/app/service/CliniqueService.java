package com.petclinic.app.service;

import com.petclinic.app.domain.Clinique;
import com.petclinic.app.repository.CliniqueRepository;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.petclinic.app.domain.Clinique}.
 */
@Service
@Transactional
public class CliniqueService {

    private static final Logger LOG = LoggerFactory.getLogger(CliniqueService.class);

    private final CliniqueRepository cliniqueRepository;

    public CliniqueService(CliniqueRepository cliniqueRepository) {
        this.cliniqueRepository = cliniqueRepository;
    }

    /**
     * Save a clinique.
     *
     * @param clinique the entity to save.
     * @return the persisted entity.
     */
    public Mono<Clinique> save(Clinique clinique) {
        LOG.debug("Request to save Clinique : {}", clinique);
        return cliniqueRepository.save(clinique);
    }

    /**
     * Update a clinique.
     *
     * @param clinique the entity to save.
     * @return the persisted entity.
     */
    public Mono<Clinique> update(Clinique clinique) {
        LOG.debug("Request to update Clinique : {}", clinique);
        return cliniqueRepository.save(clinique);
    }

    /**
     * Partially update a clinique.
     *
     * @param clinique the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Clinique> partialUpdate(Clinique clinique) {
        LOG.debug("Request to partially update Clinique : {}", clinique);

        return cliniqueRepository
            .findById(clinique.getId())
            .map(existingClinique -> {
                updateIfPresent(existingClinique::setNom, clinique.getNom());
                updateIfPresent(existingClinique::setAdresse, clinique.getAdresse());
                updateIfPresent(existingClinique::setTelephone, clinique.getTelephone());

                return existingClinique;
            })
            .flatMap(cliniqueRepository::save);
    }

    /**
     * Get all the cliniques.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Clinique> findAll(Pageable pageable) {
        LOG.debug("Request to get all Cliniques");
        return cliniqueRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of cliniques available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return cliniqueRepository.count();
    }

    /**
     * Get one clinique by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Clinique> findOne(Long id) {
        LOG.debug("Request to get Clinique : {}", id);
        return cliniqueRepository.findById(id);
    }

    /**
     * Delete the clinique by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Clinique : {}", id);
        return cliniqueRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
