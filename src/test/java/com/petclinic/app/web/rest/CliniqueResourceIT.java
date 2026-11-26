package com.petclinic.app.web.rest;

import static com.petclinic.app.domain.CliniqueAsserts.*;
import static com.petclinic.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.petclinic.app.IntegrationTest;
import com.petclinic.app.domain.Clinique;
import com.petclinic.app.repository.CliniqueRepository;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link CliniqueResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CliniqueResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cliniques";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CliniqueRepository cliniqueRepository;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private Clinique clinique;

    private Clinique insertedClinique;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Clinique createEntity() {
        return new Clinique().nom(DEFAULT_NOM).adresse(DEFAULT_ADRESSE).telephone(DEFAULT_TELEPHONE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Clinique createUpdatedEntity() {
        return new Clinique().nom(UPDATED_NOM).adresse(UPDATED_ADRESSE).telephone(UPDATED_TELEPHONE);
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(Clinique.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        clinique = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClinique != null) {
            cliniqueRepository.delete(insertedClinique).block();
            insertedClinique = null;
        }
        deleteEntities(template);
    }

    @Test
    void createClinique() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Clinique
        var returnedClinique = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(clinique))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Clinique.class)
            .returnResult()
            .getResponseBody();

        // Validate the Clinique in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCliniqueUpdatableFieldsEquals(returnedClinique, getPersistedClinique(returnedClinique));

        insertedClinique = returnedClinique;
    }

    @Test
    void createCliniqueWithExistingId() throws Exception {
        // Create the Clinique with an existing ID
        clinique.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(clinique))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Clinique in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        clinique.setNom(null);

        // Create the Clinique, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(clinique))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkAdresseIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        clinique.setAdresse(null);

        // Create the Clinique, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(clinique))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllCliniques() {
        // Initialize the database
        insertedClinique = cliniqueRepository.save(clinique).block();

        // Get all the cliniqueList
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
            .value(hasItem(clinique.getId().intValue()))
            .jsonPath("$.[*].nom")
            .value(hasItem(DEFAULT_NOM))
            .jsonPath("$.[*].adresse")
            .value(hasItem(DEFAULT_ADRESSE))
            .jsonPath("$.[*].telephone")
            .value(hasItem(DEFAULT_TELEPHONE));
    }

    @Test
    void getClinique() {
        // Initialize the database
        insertedClinique = cliniqueRepository.save(clinique).block();

        // Get the clinique
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, clinique.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(clinique.getId().intValue()))
            .jsonPath("$.nom")
            .value(is(DEFAULT_NOM))
            .jsonPath("$.adresse")
            .value(is(DEFAULT_ADRESSE))
            .jsonPath("$.telephone")
            .value(is(DEFAULT_TELEPHONE));
    }

    @Test
    void getNonExistingClinique() {
        // Get the clinique
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingClinique() throws Exception {
        // Initialize the database
        insertedClinique = cliniqueRepository.save(clinique).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clinique
        Clinique updatedClinique = cliniqueRepository.findById(clinique.getId()).block();
        updatedClinique.nom(UPDATED_NOM).adresse(UPDATED_ADRESSE).telephone(UPDATED_TELEPHONE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedClinique.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedClinique))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Clinique in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCliniqueToMatchAllProperties(updatedClinique);
    }

    @Test
    void putNonExistingClinique() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinique.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, clinique.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(clinique))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Clinique in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchClinique() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinique.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(clinique))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Clinique in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamClinique() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinique.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(clinique))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Clinique in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCliniqueWithPatch() throws Exception {
        // Initialize the database
        insertedClinique = cliniqueRepository.save(clinique).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clinique using partial update
        Clinique partialUpdatedClinique = new Clinique();
        partialUpdatedClinique.setId(clinique.getId());

        partialUpdatedClinique.nom(UPDATED_NOM).adresse(UPDATED_ADRESSE).telephone(UPDATED_TELEPHONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClinique.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedClinique))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Clinique in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCliniqueUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedClinique, clinique), getPersistedClinique(clinique));
    }

    @Test
    void fullUpdateCliniqueWithPatch() throws Exception {
        // Initialize the database
        insertedClinique = cliniqueRepository.save(clinique).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clinique using partial update
        Clinique partialUpdatedClinique = new Clinique();
        partialUpdatedClinique.setId(clinique.getId());

        partialUpdatedClinique.nom(UPDATED_NOM).adresse(UPDATED_ADRESSE).telephone(UPDATED_TELEPHONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClinique.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedClinique))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Clinique in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCliniqueUpdatableFieldsEquals(partialUpdatedClinique, getPersistedClinique(partialUpdatedClinique));
    }

    @Test
    void patchNonExistingClinique() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinique.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, clinique.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(clinique))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Clinique in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchClinique() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinique.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(clinique))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Clinique in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamClinique() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinique.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(clinique))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Clinique in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteClinique() {
        // Initialize the database
        insertedClinique = cliniqueRepository.save(clinique).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the clinique
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, clinique.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cliniqueRepository.count().block();
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

    protected Clinique getPersistedClinique(Clinique clinique) {
        return cliniqueRepository.findById(clinique.getId()).block();
    }

    protected void assertPersistedCliniqueToMatchAllProperties(Clinique expectedClinique) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCliniqueAllPropertiesEquals(expectedClinique, getPersistedClinique(expectedClinique));
        assertCliniqueUpdatableFieldsEquals(expectedClinique, getPersistedClinique(expectedClinique));
    }

    protected void assertPersistedCliniqueToMatchUpdatableProperties(Clinique expectedClinique) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCliniqueAllUpdatablePropertiesEquals(expectedClinique, getPersistedClinique(expectedClinique));
        assertCliniqueUpdatableFieldsEquals(expectedClinique, getPersistedClinique(expectedClinique));
    }
}
