package org.desu.volley.web.rest;

import com.codahale.metrics.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.desu.volley.domain.Organization;
import org.desu.volley.domain.SeasonTicketType;
import org.desu.volley.security.AuthoritiesConstants;
import org.desu.volley.security.SecurityUtils;
import org.desu.volley.service.SeasonTicketTypeService;
import org.desu.volley.service.UserService;
import org.desu.volley.web.rest.util.HeaderUtil;
import org.desu.volley.web.rest.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing SeasonTicketType.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class SeasonTicketTypeResource {

    @Inject
    private SeasonTicketTypeService seasonTicketTypeService;
    @Inject
    private UserService userService;
    
    /**
     * POST  /season-ticket-types : Create a new seasonTicketType.
     *
     * @param seasonTicketType the seasonTicketType to create
     * @return the ResponseEntity with status 201 (Created) and with body the new seasonTicketType, or with status 400 (Bad Request) if the seasonTicketType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/season-ticket-types",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({AuthoritiesConstants.ORGANIZER, AuthoritiesConstants.ADMIN})
    public ResponseEntity<SeasonTicketType> createSeasonTicketType(@Valid @RequestBody SeasonTicketType seasonTicketType) throws URISyntaxException {
        log.debug("REST request to save SeasonTicketType : {}", seasonTicketType);
        if (seasonTicketType.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("seasonTicketType", "idexists", "A new seasonTicketType cannot already have an ID")).body(null);
        }
        SeasonTicketType result = seasonTicketTypeService.save(seasonTicketType);
        return ResponseEntity.created(new URI("/api/season-ticket-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("seasonTicketType", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /season-ticket-types : Updates an existing seasonTicketType.
     *
     * @param seasonTicketType the seasonTicketType to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated seasonTicketType,
     * or with status 400 (Bad Request) if the seasonTicketType is not valid,
     * or with status 500 (Internal Server Error) if the seasonTicketType couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/season-ticket-types",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({AuthoritiesConstants.ORGANIZER, AuthoritiesConstants.ADMIN})
    public ResponseEntity<SeasonTicketType> updateSeasonTicketType(@Valid @RequestBody SeasonTicketType seasonTicketType) throws URISyntaxException {
        log.debug("REST request to update SeasonTicketType : {}", seasonTicketType);
        if (seasonTicketType.getId() == null) {
            return createSeasonTicketType(seasonTicketType);
        }

        if (canModifySeasonTicketType(seasonTicketType.getId())) {
            SeasonTicketType result = seasonTicketTypeService.save(seasonTicketType);
            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("seasonTicketType", seasonTicketType.getId().toString()))
                .body(result);
        }
        return ResponseEntity.badRequest()
            .headers(HeaderUtil.createFailureAlert("seasonTicketType", "accessdenied", "Access denied"))
            .body(seasonTicketType);
    }

    /**
     * GET  /season-ticket-types : get all the seasonTicketTypes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of seasonTicketTypes in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/season-ticket-types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SeasonTicketType>> getAllSeasonTicketTypes(
        Pageable pageable,
        @RequestParam(required = false, value = "organizationId") Organization organization) throws URISyntaxException {

        log.debug("REST request to get a page of SeasonTicketTypes");

        Page<SeasonTicketType> page;
        if (organization == null) {
            page = seasonTicketTypeService.findAll(pageable);
        } else {
            page = seasonTicketTypeService.findByOrganization(organization, pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/season-ticket-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /season-ticket-types/:id : get the "id" seasonTicketType.
     *
     * @param id the id of the seasonTicketType to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the seasonTicketType, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/season-ticket-types/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SeasonTicketType> getSeasonTicketType(@PathVariable Long id) {
        log.debug("REST request to get SeasonTicketType : {}", id);
        SeasonTicketType seasonTicketType = seasonTicketTypeService.findOne(id);
        return Optional.ofNullable(seasonTicketType)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /season-ticket-types/:id : delete the "id" seasonTicketType.
     *
     * @param id the id of the seasonTicketType to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/season-ticket-types/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({AuthoritiesConstants.ORGANIZER, AuthoritiesConstants.ADMIN})
    public ResponseEntity<Void> deleteSeasonTicketType(@PathVariable Long id) {
        log.debug("REST request to delete SeasonTicketType : {}", id);

        if (canModifySeasonTicketType(id)) {
            seasonTicketTypeService.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("seasonTicketType", id.toString())).build();
        }
        return ResponseEntity.badRequest()
            .headers(HeaderUtil.createFailureAlert("seasonTicketType", "accessdenied", "Access denied"))
            .build();
    }

    private boolean canModifySeasonTicketType(Long id) {
        return SecurityUtils.isCurrentUserInOrganizationOrAdmin(() -> seasonTicketTypeService.findOne(id).getOrganization(), userService);
    }
}
