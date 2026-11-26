package com.petclinic.app.web.rest;

import static com.petclinic.app.domain.AnimalAsserts.*;
import static com.petclinic.app.web.rest.TestUtil.createUpdateProxyForBean;
import static com.petclinic.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.petclinic.app.IntegrationTest;
import com.petclinic.app.domain.Animal;
import com.petclinic.app.repository.AnimalRepository;
import com.petclinic.app.service.AnimalService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link AnimalResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AnimalResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_ESPECE = "AAAAAAAAAA";
    private static final String UPDATED_ESPECE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_NAISSANCE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_NAISSANCE = LocalDate.parse("2026-11-26");

    private static final BigDecimal DEFAULT_POIDS = new BigDecimal(0);
    private static final BigDecimal UPDATED_POIDS = new BigDecimal(1);

    private static final String ENTITY_API_URL = "/api/animals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AnimalRepository animalRepository;

    @Mock
    private AnimalRepository animalRepositoryMock;

    @Mock
    private AnimalService animalServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private Animal animal;

    private Animal insertedAnimal;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Animal createEntity() {
        return new Animal().nom(DEFAULT_NOM).espece(DEFAULT_ESPECE).dateNaissance(DEFAULT_DATE_NAISSANCE).poids(DEFAULT_POIDS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Animal createUpdatedEntity() {
        return new Animal().nom(UPDATED_NOM).espece(UPDATED_ESPECE).dateNaissance(UPDATED_DATE_NAISSANCE).poids(UPDATED_POIDS);
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(Animal.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        animal = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAnimal != null) {
            animalRepository.delete(insertedAnimal).block();
            insertedAnimal = null;
        }
        deleteEntities(template);
    }

    @Test
    void createAnimal() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Animal
        var returnedAnimal = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(animal))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Animal.class)
            .returnResult()
            .getResponseBody();

        // Validate the Animal in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAnimalUpdatableFieldsEquals(returnedAnimal, getPersistedAnimal(returnedAnimal));

        insertedAnimal = returnedAnimal;
    }

    @Test
    void createAnimalWithExistingId() throws Exception {
        // Create the Animal with an existing ID
        animal.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(animal))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Animal in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        animal.setNom(null);

        // Create the Animal, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(animal))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEspeceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        animal.setEspece(null);

        // Create the Animal, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(animal))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAnimals() {
        // Initialize the database
        insertedAnimal = animalRepository.save(animal).block();

        // Get all the animalList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(animal.getId().intValue()))
            .jsonPath("$.[*].nom")
            .value(hasItem(DEFAULT_NOM))
            .jsonPath("$.[*].espece")
            .value(hasItem(DEFAULT_ESPECE))
            .jsonPath("$.[*].dateNaissance")
            .value(hasItem(DEFAULT_DATE_NAISSANCE.toString()))
            .jsonPath("$.[*].poids")
            .value(hasItem(sameNumber(DEFAULT_POIDS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAnimalsWithEagerRelationshipsIsEnabled() {
        when(animalServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(animalServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAnimalsWithEagerRelationshipsIsNotEnabled() {
        when(animalServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(animalRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getAnimal() {
        // Initialize the database
        insertedAnimal = animalRepository.save(animal).block();

        // Get the animal
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, animal.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(animal.getId().intValue()))
            .jsonPath("$.nom")
            .value(is(DEFAULT_NOM))
            .jsonPath("$.espece")
            .value(is(DEFAULT_ESPECE))
            .jsonPath("$.dateNaissance")
            .value(is(DEFAULT_DATE_NAISSANCE.toString()))
            .jsonPath("$.poids")
            .value(is(sameNumber(DEFAULT_POIDS)));
    }

    @Test
    void getNonExistingAnimal() {
        // Get the animal
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAnimal() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.save(animal).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the animal
        Animal updatedAnimal = animalRepository.findById(animal.getId()).block();
        updatedAnimal.nom(UPDATED_NOM).espece(UPDATED_ESPECE).dateNaissance(UPDATED_DATE_NAISSANCE).poids(UPDATED_POIDS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAnimal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedAnimal))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Animal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAnimalToMatchAllProperties(updatedAnimal);
    }

    @Test
    void putNonExistingAnimal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        animal.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, animal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(animal))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Animal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAnimal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        animal.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(animal))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Animal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAnimal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        animal.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(animal))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Animal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAnimalWithPatch() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.save(animal).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the animal using partial update
        Animal partialUpdatedAnimal = new Animal();
        partialUpdatedAnimal.setId(animal.getId());

        partialUpdatedAnimal.nom(UPDATED_NOM).espece(UPDATED_ESPECE).poids(UPDATED_POIDS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAnimal.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAnimal))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Animal in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAnimalUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAnimal, animal), getPersistedAnimal(animal));
    }

    @Test
    void fullUpdateAnimalWithPatch() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.save(animal).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the animal using partial update
        Animal partialUpdatedAnimal = new Animal();
        partialUpdatedAnimal.setId(animal.getId());

        partialUpdatedAnimal.nom(UPDATED_NOM).espece(UPDATED_ESPECE).dateNaissance(UPDATED_DATE_NAISSANCE).poids(UPDATED_POIDS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAnimal.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAnimal))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Animal in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAnimalUpdatableFieldsEquals(partialUpdatedAnimal, getPersistedAnimal(partialUpdatedAnimal));
    }

    @Test
    void patchNonExistingAnimal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        animal.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, animal.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(animal))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Animal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAnimal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        animal.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(animal))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Animal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAnimal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        animal.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(animal))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Animal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAnimal() {
        // Initialize the database
        insertedAnimal = animalRepository.save(animal).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the animal
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, animal.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return animalRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Animal getPersistedAnimal(Animal animal) {
        return animalRepository.findById(animal.getId()).block();
    }

    protected void assertPersistedAnimalToMatchAllProperties(Animal expectedAnimal) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAnimalAllPropertiesEquals(expectedAnimal, getPersistedAnimal(expectedAnimal));
        assertAnimalUpdatableFieldsEquals(expectedAnimal, getPersistedAnimal(expectedAnimal));
    }

    protected void assertPersistedAnimalToMatchUpdatableProperties(Animal expectedAnimal) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAnimalAllUpdatablePropertiesEquals(expectedAnimal, getPersistedAnimal(expectedAnimal));
        assertAnimalUpdatableFieldsEquals(expectedAnimal, getPersistedAnimal(expectedAnimal));
    }
}
