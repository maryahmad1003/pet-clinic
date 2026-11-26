package com.petclinic.app.web.rest;

import com.petclinic.app.domain.Clinique;
import com.petclinic.app.repository.CliniqueRepository;
import com.petclinic.app.service.CliniqueService;
import com.petclinic.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.petclinic.app.domain.Clinique}.
 */
@RestController
@RequestMapping("/api/cliniques")
public class CliniqueResource {

    private static final Logger LOG = LoggerFactory.getLogger(CliniqueResource.class);

    private static final String ENTITY_NAME = "clinique";

    @Value("${jhipster.clientApp.name:petclinic}")
    private String applicationName;

    private final CliniqueService cliniqueService;

    private final CliniqueRepository cliniqueRepository;

    public CliniqueResource(CliniqueService cliniqueService, CliniqueRepository cliniqueRepository) {
        this.cliniqueService = cliniqueService;
        this.cliniqueRepository = cliniqueRepository;
    }

    /**
     * {@code POST  /cliniques} : Create a new clinique.
     *
     * @param clinique the clinique to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clinique, or with status {@code 400 (Bad Request)} if the clinique already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Clinique>> createClinique(@Valid @RequestBody Clinique clinique) throws URISyntaxException {
        LOG.debug("REST request to save Clinique : {}", clinique);
        if (clinique.getId() != null) {
            throw new BadRequestAlertException("A new clinique cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return cliniqueService.save(clinique).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/cliniques/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /cliniques/:id} : Updates an existing clinique.
     *
     * @param id the id of the clinique to save.
     * @param clinique the clinique to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clinique,
     * or with status {@code 400 (Bad Request)} if the clinique is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clinique couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Clinique>> updateClinique(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Clinique clinique
    ) throws URISyntaxException {
        LOG.debug("REST request to update Clinique : {}, {}", id, clinique);
        if (clinique.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clinique.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return cliniqueRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return cliniqueService
                .update(clinique)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /cliniques/:id} : Partial updates given fields of an existing clinique, field will ignore if it is null
     *
     * @param id the id of the clinique to save.
     * @param clinique the clinique to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clinique,
     * or with status {@code 400 (Bad Request)} if the clinique is not valid,
     * or with status {@code 404 (Not Found)} if the clinique is not found,
     * or with status {@code 500 (Internal Server Error)} if the clinique couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Clinique>> partialUpdateClinique(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Clinique clinique
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update Clinique : {}, {}", id, clinique);
        if (clinique.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clinique.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return cliniqueRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<Clinique> result = cliniqueService.partialUpdate(clinique);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /cliniques} : get all the Cliniques.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Cliniques in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Clinique>>> getAllCliniques(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Cliniques");
        return cliniqueService
            .countAll()
            .zipWith(cliniqueService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /cliniques/:id} : get the "id" clinique.
     *
     * @param id the id of the clinique to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clinique, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Clinique>> getClinique(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Clinique : {}", id);
        Mono<Clinique> clinique = cliniqueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clinique);
    }

    /**
     * {@code DELETE  /cliniques/:id} : delete the "id" clinique.
     *
     * @param id the id of the clinique to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteClinique(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Clinique : {}", id);
        return cliniqueService
            .delete(id)

            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
