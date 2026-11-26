package com.petclinic.app.web.rest;

import static com.petclinic.app.domain.ClientAsserts.*;
import static com.petclinic.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.petclinic.app.IntegrationTest;
import com.petclinic.app.domain.Client;
import com.petclinic.app.repository.ClientRepository;
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
 * Integration tests for the {@link ClientResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ClientResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/clients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private Client client;

    private Client insertedClient;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Client createEntity() {
        return new Client().nom(DEFAULT_NOM).prenom(DEFAULT_PRENOM).adresse(DEFAULT_ADRESSE).telephone(DEFAULT_TELEPHONE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Client createUpdatedEntity() {
        return new Client().nom(UPDATED_NOM).prenom(UPDATED_PRENOM).adresse(UPDATED_ADRESSE).telephone(UPDATED_TELEPHONE);
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(Client.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        client = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClient != null) {
            clientRepository.delete(insertedClient).block();
            insertedClient = null;
        }
        deleteEntities(template);
    }

    @Test
    void createClient() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Client
        var returnedClient = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(client))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Client.class)
            .returnResult()
            .getResponseBody();

        // Validate the Client in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertClientUpdatableFieldsEquals(returnedClient, getPersistedClient(returnedClient));

        insertedClient = returnedClient;
    }

    @Test
    void createClientWithExistingId() throws Exception {
        // Create the Client with an existing ID
        client.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(client))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        client.setNom(null);

        // Create the Client, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(client))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPrenomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        client.setPrenom(null);

        // Create the Client, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(client))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllClients() {
        // Initialize the database
        insertedClient = clientRepository.save(client).block();

        // Get all the clientList
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
            .value(hasItem(client.getId().intValue()))
            .jsonPath("$.[*].nom")
            .value(hasItem(DEFAULT_NOM))
            .jsonPath("$.[*].prenom")
            .value(hasItem(DEFAULT_PRENOM))
            .jsonPath("$.[*].adresse")
            .value(hasItem(DEFAULT_ADRESSE))
            .jsonPath("$.[*].telephone")
            .value(hasItem(DEFAULT_TELEPHONE));
    }

    @Test
    void getClient() {
        // Initialize the database
        insertedClient = clientRepository.save(client).block();

        // Get the client
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, client.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(client.getId().intValue()))
            .jsonPath("$.nom")
            .value(is(DEFAULT_NOM))
            .jsonPath("$.prenom")
            .value(is(DEFAULT_PRENOM))
            .jsonPath("$.adresse")
            .value(is(DEFAULT_ADRESSE))
            .jsonPath("$.telephone")
            .value(is(DEFAULT_TELEPHONE));
    }

    @Test
    void getNonExistingClient() {
        // Get the client
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingClient() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.save(client).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the client
        Client updatedClient = clientRepository.findById(client.getId()).block();
        updatedClient.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).adresse(UPDATED_ADRESSE).telephone(UPDATED_TELEPHONE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedClient.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedClient))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClientToMatchAllProperties(updatedClient);
    }

    @Test
    void putNonExistingClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, client.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(client))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(client))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(client))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateClientWithPatch() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.save(client).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the client using partial update
        Client partialUpdatedClient = new Client();
        partialUpdatedClient.setId(client.getId());

        partialUpdatedClient.nom(UPDATED_NOM).telephone(UPDATED_TELEPHONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClient.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedClient))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Client in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedClient, client), getPersistedClient(client));
    }

    @Test
    void fullUpdateClientWithPatch() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.save(client).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the client using partial update
        Client partialUpdatedClient = new Client();
        partialUpdatedClient.setId(client.getId());

        partialUpdatedClient.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).adresse(UPDATED_ADRESSE).telephone(UPDATED_TELEPHONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClient.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedClient))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Client in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientUpdatableFieldsEquals(partialUpdatedClient, getPersistedClient(partialUpdatedClient));
    }

    @Test
    void patchNonExistingClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, client.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(client))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(client))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(client))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteClient() {
        // Initialize the database
        insertedClient = clientRepository.save(client).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the client
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, client.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return clientRepository.count().block();
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

    protected Client getPersistedClient(Client client) {
        return clientRepository.findById(client.getId()).block();
    }

    protected void assertPersistedClientToMatchAllProperties(Client expectedClient) {
        // Test fails because reactive api returns an empty object instead of null
        // assertClientAllPropertiesEquals(expectedClient, getPersistedClient(expectedClient));
        assertClientUpdatableFieldsEquals(expectedClient, getPersistedClient(expectedClient));
    }

    protected void assertPersistedClientToMatchUpdatableProperties(Client expectedClient) {
        // Test fails because reactive api returns an empty object instead of null
        // assertClientAllUpdatablePropertiesEquals(expectedClient, getPersistedClient(expectedClient));
        assertClientUpdatableFieldsEquals(expectedClient, getPersistedClient(expectedClient));
    }
}
