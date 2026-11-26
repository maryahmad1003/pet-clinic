package com.petclinic.app.service;

import com.petclinic.app.domain.Animal;
import com.petclinic.app.repository.AnimalRepository;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.petclinic.app.domain.Animal}.
 */
@Service
@Transactional
public class AnimalService {

    private static final Logger LOG = LoggerFactory.getLogger(AnimalService.class);

    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    /**
     * Save a animal.
     *
     * @param animal the entity to save.
     * @return the persisted entity.
     */
    public Mono<Animal> save(Animal animal) {
        LOG.debug("Request to save Animal : {}", animal);
        return animalRepository.save(animal);
    }

    /**
     * Update a animal.
     *
     * @param animal the entity to save.
     * @return the persisted entity.
     */
    public Mono<Animal> update(Animal animal) {
        LOG.debug("Request to update Animal : {}", animal);
        return animalRepository.save(animal);
    }

    /**
     * Partially update a animal.
     *
     * @param animal the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Animal> partialUpdate(Animal animal) {
        LOG.debug("Request to partially update Animal : {}", animal);

        return animalRepository
            .findById(animal.getId())
            .map(existingAnimal -> {
                updateIfPresent(existingAnimal::setNom, animal.getNom());
                updateIfPresent(existingAnimal::setEspece, animal.getEspece());
                updateIfPresent(existingAnimal::setDateNaissance, animal.getDateNaissance());
                updateIfPresent(existingAnimal::setPoids, animal.getPoids());

                return existingAnimal;
            })
            .flatMap(animalRepository::save);
    }

    /**
     * Get all the animals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Animal> findAll(Pageable pageable) {
        LOG.debug("Request to get all Animals");
        return animalRepository.findAllBy(pageable);
    }

    /**
     * Get all the animals with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<Animal> findAllWithEagerRelationships(Pageable pageable) {
        return animalRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Returns the number of animals available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return animalRepository.count();
    }

    /**
     * Get one animal by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Animal> findOne(Long id) {
        LOG.debug("Request to get Animal : {}", id);
        return animalRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the animal by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Animal : {}", id);
        return animalRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
