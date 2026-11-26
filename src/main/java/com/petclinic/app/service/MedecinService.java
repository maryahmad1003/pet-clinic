package com.petclinic.app.service;

import com.petclinic.app.domain.Medecin;
import com.petclinic.app.repository.MedecinRepository;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.petclinic.app.domain.Medecin}.
 */
@Service
@Transactional
public class MedecinService {

    private static final Logger LOG = LoggerFactory.getLogger(MedecinService.class);

    private final MedecinRepository medecinRepository;

    public MedecinService(MedecinRepository medecinRepository) {
        this.medecinRepository = medecinRepository;
    }

    /**
     * Save a medecin.
     *
     * @param medecin the entity to save.
     * @return the persisted entity.
     */
    public Mono<Medecin> save(Medecin medecin) {
        LOG.debug("Request to save Medecin : {}", medecin);
        return medecinRepository.save(medecin);
    }

    /**
     * Update a medecin.
     *
     * @param medecin the entity to save.
     * @return the persisted entity.
     */
    public Mono<Medecin> update(Medecin medecin) {
        LOG.debug("Request to update Medecin : {}", medecin);
        return medecinRepository.save(medecin);
    }

    /**
     * Partially update a medecin.
     *
     * @param medecin the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Medecin> partialUpdate(Medecin medecin) {
        LOG.debug("Request to partially update Medecin : {}", medecin);

        return medecinRepository
            .findById(medecin.getId())
            .map(existingMedecin -> {
                updateIfPresent(existingMedecin::setNom, medecin.getNom());
                updateIfPresent(existingMedecin::setPrenom, medecin.getPrenom());
                updateIfPresent(existingMedecin::setSpecialite, medecin.getSpecialite());
                updateIfPresent(existingMedecin::setEmail, medecin.getEmail());

                return existingMedecin;
            })
            .flatMap(medecinRepository::save);
    }

    /**
     * Get all the medecins.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Medecin> findAll(Pageable pageable) {
        LOG.debug("Request to get all Medecins");
        return medecinRepository.findAllBy(pageable);
    }

    /**
     * Get all the medecins with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<Medecin> findAllWithEagerRelationships(Pageable pageable) {
        return medecinRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Returns the number of medecins available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return medecinRepository.count();
    }

    /**
     * Get one medecin by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Medecin> findOne(Long id) {
        LOG.debug("Request to get Medecin : {}", id);
        return medecinRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the medecin by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Medecin : {}", id);
        return medecinRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
