package com.petclinic.app.web.rest;

import static com.petclinic.app.domain.RendezVousAsserts.*;
import static com.petclinic.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.petclinic.app.IntegrationTest;
import com.petclinic.app.domain.RendezVous;
import com.petclinic.app.repository.RendezVousRepository;
import com.petclinic.app.service.RendezVousService;
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
 * Integration tests for the {@link RendezVousResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RendezVousResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.parse("2026-11-26");

    private static final String DEFAULT_HEURE = "10:04";
    private static final String UPDATED_HEURE = "13:43";

    private static final String DEFAULT_MOTIF = "AAAAAAAAAA";
    private static final String UPDATED_MOTIF = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/rendez-vous";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Mock
    private RendezVousRepository rendezVousRepositoryMock;

    @Mock
    private RendezVousService rendezVousServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private RendezVous rendezVous;

    private RendezVous insertedRendezVous;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RendezVous createEntity() {
        return new RendezVous().date(DEFAULT_DATE).heure(DEFAULT_HEURE).motif(DEFAULT_MOTIF);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RendezVous createUpdatedEntity() {
        return new RendezVous().date(UPDATED_DATE).heure(UPDATED_HEURE).motif(UPDATED_MOTIF);
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(RendezVous.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        rendezVous = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRendezVous != null) {
            rendezVousRepository.delete(insertedRendezVous).block();
            insertedRendezVous = null;
        }
        deleteEntities(template);
    }

    @Test
    void createRendezVous() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RendezVous
        var returnedRendezVous = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rendezVous))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(RendezVous.class)
            .returnResult()
            .getResponseBody();

        // Validate the RendezVous in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRendezVousUpdatableFieldsEquals(returnedRendezVous, getPersistedRendezVous(returnedRendezVous));

        insertedRendezVous = returnedRendezVous;
    }

    @Test
    void createRendezVousWithExistingId() throws Exception {
        // Create the RendezVous with an existing ID
        rendezVous.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rendezVous))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RendezVous in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rendezVous.setDate(null);

        // Create the RendezVous, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rendezVous))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkHeureIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rendezVous.setHeure(null);

        // Create the RendezVous, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rendezVous))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkMotifIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rendezVous.setMotif(null);

        // Create the RendezVous, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rendezVous))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllRendezVouses() {
        // Initialize the database
        insertedRendezVous = rendezVousRepository.save(rendezVous).block();

        // Get all the rendezVousList
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
            .value(hasItem(rendezVous.getId().intValue()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].heure")
            .value(hasItem(DEFAULT_HEURE))
            .jsonPath("$.[*].motif")
            .value(hasItem(DEFAULT_MOTIF));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRendezVousesWithEagerRelationshipsIsEnabled() {
        when(rendezVousServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(rendezVousServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRendezVousesWithEagerRelationshipsIsNotEnabled() {
        when(rendezVousServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(rendezVousRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getRendezVous() {
        // Initialize the database
        insertedRendezVous = rendezVousRepository.save(rendezVous).block();

        // Get the rendezVous
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rendezVous.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rendezVous.getId().intValue()))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()))
            .jsonPath("$.heure")
            .value(is(DEFAULT_HEURE))
            .jsonPath("$.motif")
            .value(is(DEFAULT_MOTIF));
    }

    @Test
    void getNonExistingRendezVous() {
        // Get the rendezVous
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRendezVous() throws Exception {
        // Initialize the database
        insertedRendezVous = rendezVousRepository.save(rendezVous).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rendezVous
        RendezVous updatedRendezVous = rendezVousRepository.findById(rendezVous.getId()).block();
        updatedRendezVous.date(UPDATED_DATE).heure(UPDATED_HEURE).motif(UPDATED_MOTIF);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRendezVous.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedRendezVous))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RendezVous in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRendezVousToMatchAllProperties(updatedRendezVous);
    }

    @Test
    void putNonExistingRendezVous() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rendezVous.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rendezVous.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rendezVous))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RendezVous in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRendezVous() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rendezVous.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rendezVous))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RendezVous in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRendezVous() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rendezVous.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rendezVous))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RendezVous in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRendezVousWithPatch() throws Exception {
        // Initialize the database
        insertedRendezVous = rendezVousRepository.save(rendezVous).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rendezVous using partial update
        RendezVous partialUpdatedRendezVous = new RendezVous();
        partialUpdatedRendezVous.setId(rendezVous.getId());

        partialUpdatedRendezVous.date(UPDATED_DATE).motif(UPDATED_MOTIF);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRendezVous.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRendezVous))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RendezVous in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRendezVousUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRendezVous, rendezVous),
            getPersistedRendezVous(rendezVous)
        );
    }

    @Test
    void fullUpdateRendezVousWithPatch() throws Exception {
        // Initialize the database
        insertedRendezVous = rendezVousRepository.save(rendezVous).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rendezVous using partial update
        RendezVous partialUpdatedRendezVous = new RendezVous();
        partialUpdatedRendezVous.setId(rendezVous.getId());

        partialUpdatedRendezVous.date(UPDATED_DATE).heure(UPDATED_HEURE).motif(UPDATED_MOTIF);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRendezVous.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRendezVous))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RendezVous in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRendezVousUpdatableFieldsEquals(partialUpdatedRendezVous, getPersistedRendezVous(partialUpdatedRendezVous));
    }

    @Test
    void patchNonExistingRendezVous() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rendezVous.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rendezVous.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(rendezVous))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RendezVous in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRendezVous() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rendezVous.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(rendezVous))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RendezVous in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRendezVous() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rendezVous.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(rendezVous))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RendezVous in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRendezVous() {
        // Initialize the database
        insertedRendezVous = rendezVousRepository.save(rendezVous).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the rendezVous
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rendezVous.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return rendezVousRepository.count().block();
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

    protected RendezVous getPersistedRendezVous(RendezVous rendezVous) {
        return rendezVousRepository.findById(rendezVous.getId()).block();
    }

    protected void assertPersistedRendezVousToMatchAllProperties(RendezVous expectedRendezVous) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRendezVousAllPropertiesEquals(expectedRendezVous, getPersistedRendezVous(expectedRendezVous));
        assertRendezVousUpdatableFieldsEquals(expectedRendezVous, getPersistedRendezVous(expectedRendezVous));
    }

    protected void assertPersistedRendezVousToMatchUpdatableProperties(RendezVous expectedRendezVous) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRendezVousAllUpdatablePropertiesEquals(expectedRendezVous, getPersistedRendezVous(expectedRendezVous));
        assertRendezVousUpdatableFieldsEquals(expectedRendezVous, getPersistedRendezVous(expectedRendezVous));
    }
}
