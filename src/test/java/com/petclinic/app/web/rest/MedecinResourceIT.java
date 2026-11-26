package com.petclinic.app.web.rest;

import static com.petclinic.app.domain.MedecinAsserts.*;
import static com.petclinic.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.petclinic.app.IntegrationTest;
import com.petclinic.app.domain.Medecin;
import com.petclinic.app.repository.MedecinRepository;
import com.petclinic.app.service.MedecinService;
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
 * Integration tests for the {@link MedecinResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MedecinResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final String DEFAULT_SPECIALITE = "AAAAAAAAAA";
    private static final String UPDATED_SPECIALITE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/medecins";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MedecinRepository medecinRepository;

    @Mock
    private MedecinRepository medecinRepositoryMock;

    @Mock
    private MedecinService medecinServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private Medecin medecin;

    private Medecin insertedMedecin;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Medecin createEntity() {
        return new Medecin().nom(DEFAULT_NOM).prenom(DEFAULT_PRENOM).specialite(DEFAULT_SPECIALITE).email(DEFAULT_EMAIL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Medecin createUpdatedEntity() {
        return new Medecin().nom(UPDATED_NOM).prenom(UPDATED_PRENOM).specialite(UPDATED_SPECIALITE).email(UPDATED_EMAIL);
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(Medecin.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        medecin = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMedecin != null) {
            medecinRepository.delete(insertedMedecin).block();
            insertedMedecin = null;
        }
        deleteEntities(template);
    }

    @Test
    void createMedecin() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Medecin
        var returnedMedecin = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(medecin))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Medecin.class)
            .returnResult()
            .getResponseBody();

        // Validate the Medecin in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMedecinUpdatableFieldsEquals(returnedMedecin, getPersistedMedecin(returnedMedecin));

        insertedMedecin = returnedMedecin;
    }

    @Test
    void createMedecinWithExistingId() throws Exception {
        // Create the Medecin with an existing ID
        medecin.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(medecin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Medecin in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        medecin.setNom(null);

        // Create the Medecin, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(medecin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPrenomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        medecin.setPrenom(null);

        // Create the Medecin, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(medecin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSpecialiteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        medecin.setSpecialite(null);

        // Create the Medecin, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(medecin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        medecin.setEmail(null);

        // Create the Medecin, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(medecin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllMedecins() {
        // Initialize the database
        insertedMedecin = medecinRepository.save(medecin).block();

        // Get all the medecinList
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
            .value(hasItem(medecin.getId().intValue()))
            .jsonPath("$.[*].nom")
            .value(hasItem(DEFAULT_NOM))
            .jsonPath("$.[*].prenom")
            .value(hasItem(DEFAULT_PRENOM))
            .jsonPath("$.[*].specialite")
            .value(hasItem(DEFAULT_SPECIALITE))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMedecinsWithEagerRelationshipsIsEnabled() {
        when(medecinServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(medecinServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMedecinsWithEagerRelationshipsIsNotEnabled() {
        when(medecinServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(medecinRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMedecin() {
        // Initialize the database
        insertedMedecin = medecinRepository.save(medecin).block();

        // Get the medecin
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, medecin.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(medecin.getId().intValue()))
            .jsonPath("$.nom")
            .value(is(DEFAULT_NOM))
            .jsonPath("$.prenom")
            .value(is(DEFAULT_PRENOM))
            .jsonPath("$.specialite")
            .value(is(DEFAULT_SPECIALITE))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL));
    }

    @Test
    void getNonExistingMedecin() {
        // Get the medecin
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMedecin() throws Exception {
        // Initialize the database
        insertedMedecin = medecinRepository.save(medecin).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the medecin
        Medecin updatedMedecin = medecinRepository.findById(medecin.getId()).block();
        updatedMedecin.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).specialite(UPDATED_SPECIALITE).email(UPDATED_EMAIL);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedMedecin.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedMedecin))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Medecin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMedecinToMatchAllProperties(updatedMedecin);
    }

    @Test
    void putNonExistingMedecin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medecin.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, medecin.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(medecin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Medecin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMedecin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medecin.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(medecin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Medecin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMedecin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medecin.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(medecin))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Medecin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMedecinWithPatch() throws Exception {
        // Initialize the database
        insertedMedecin = medecinRepository.save(medecin).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the medecin using partial update
        Medecin partialUpdatedMedecin = new Medecin();
        partialUpdatedMedecin.setId(medecin.getId());

        partialUpdatedMedecin.email(UPDATED_EMAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMedecin.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMedecin))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Medecin in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMedecinUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMedecin, medecin), getPersistedMedecin(medecin));
    }

    @Test
    void fullUpdateMedecinWithPatch() throws Exception {
        // Initialize the database
        insertedMedecin = medecinRepository.save(medecin).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the medecin using partial update
        Medecin partialUpdatedMedecin = new Medecin();
        partialUpdatedMedecin.setId(medecin.getId());

        partialUpdatedMedecin.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).specialite(UPDATED_SPECIALITE).email(UPDATED_EMAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMedecin.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMedecin))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Medecin in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMedecinUpdatableFieldsEquals(partialUpdatedMedecin, getPersistedMedecin(partialUpdatedMedecin));
    }

    @Test
    void patchNonExistingMedecin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medecin.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, medecin.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(medecin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Medecin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMedecin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medecin.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(medecin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Medecin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMedecin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medecin.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(medecin))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Medecin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMedecin() {
        // Initialize the database
        insertedMedecin = medecinRepository.save(medecin).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the medecin
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, medecin.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return medecinRepository.count().block();
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

    protected Medecin getPersistedMedecin(Medecin medecin) {
        return medecinRepository.findById(medecin.getId()).block();
    }

    protected void assertPersistedMedecinToMatchAllProperties(Medecin expectedMedecin) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMedecinAllPropertiesEquals(expectedMedecin, getPersistedMedecin(expectedMedecin));
        assertMedecinUpdatableFieldsEquals(expectedMedecin, getPersistedMedecin(expectedMedecin));
    }

    protected void assertPersistedMedecinToMatchUpdatableProperties(Medecin expectedMedecin) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMedecinAllUpdatablePropertiesEquals(expectedMedecin, getPersistedMedecin(expectedMedecin));
        assertMedecinUpdatableFieldsEquals(expectedMedecin, getPersistedMedecin(expectedMedecin));
    }
}
