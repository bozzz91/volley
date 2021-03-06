package org.desu.volley.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.desu.volley.domain.Gym;
import org.desu.volley.domain.Organization;
import org.desu.volley.repository.GymRepository;
import org.desu.volley.security.AuthoritiesConstants;
import org.desu.volley.security.SecurityUtils;
import org.desu.volley.service.UserService;
import org.desu.volley.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Gym.
 */
@RestController
@RequestMapping("/api")
public class GymResource {

    private final Logger log = LoggerFactory.getLogger(GymResource.class);

    @Inject
    private GymRepository gymRepository;
    @Inject
    private UserService userService;

    /**
     * POST  /gyms : Create a new gym.
     *
     * @param gym the gym to create
     * @return the ResponseEntity with status 201 (Created) and with body the new gym, or with status 400 (Bad Request) if the gym has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/gyms",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({AuthoritiesConstants.ORGANIZER, AuthoritiesConstants.ADMIN})
    public ResponseEntity<Gym> createGym(@Valid @RequestBody Gym gym) throws URISyntaxException {
        log.debug("REST request to save Gym : {}", gym);
        if (gym.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("gym", "idexists", "A new gym cannot already have an ID")).body(null);
        }
        Gym result = gymRepository.save(gym);
        return ResponseEntity.created(new URI("/api/gyms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("gym", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /gyms : Updates an existing gym.
     *
     * @param gym the gym to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated gym,
     * or with status 400 (Bad Request) if the gym is not valid,
     * or with status 500 (Internal Server Error) if the gym couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/gyms",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({AuthoritiesConstants.ORGANIZER, AuthoritiesConstants.ADMIN})
    public ResponseEntity<Gym> updateGym(@Valid @RequestBody Gym gym) throws URISyntaxException {
        log.debug("REST request to update Gym : {}", gym);
        if (gym.getId() == null) {
            return createGym(gym);
        }
        if (canModifyGym(gym.getId())) {
            Gym result = gymRepository.save(gym);
            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("gym", gym.getId().toString()))
                .body(result);
        }
        return ResponseEntity.badRequest()
            .headers(HeaderUtil.createFailureAlert("gym", "accessdenied", "Access denied"))
            .body(gym);
    }

    /**
     * GET  /gyms : get all the gyms.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of gyms in body
     */
    @RequestMapping(value = "/gyms",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Gym> getAllGyms(Sort sort, @RequestParam(required = false, value = "organizationId") Organization organization) {
        log.debug("REST request to get Gyms");
        List<Gym> gyms;
        if (organization == null) {
            gyms = gymRepository.findAll(sort);
        } else {
            gyms = gymRepository.findByOrganization(organization, sort);
        }
        return gyms;
    }

    /**
     * GET  /gyms/:id : get the "id" gym.
     *
     * @param id the id of the gym to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the gym, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/gyms/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Gym> getGym(@PathVariable Long id) {
        log.debug("REST request to get Gym : {}", id);
        Gym gym = gymRepository.findOne(id);
        return Optional.ofNullable(gym)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /gyms/:id : delete the "id" gym.
     *
     * @param id the id of the gym to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/gyms/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    @Secured({AuthoritiesConstants.ORGANIZER, AuthoritiesConstants.ADMIN})
    public ResponseEntity<Void> deleteGym(@PathVariable Long id) {
        log.debug("REST request to delete Gym : {}", id);
        if (canModifyGym(id)) {
            gymRepository.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("gym", id.toString())).build();
        }
        return ResponseEntity.badRequest()
            .headers(HeaderUtil.createFailureAlert("gym", "accessdenied", "Access denied"))
            .build();
    }

    private boolean canModifyGym(Long id) {
        return SecurityUtils.isCurrentUserInOrganizationOrAdmin(() -> gymRepository.findOne(id).getOrganization(), userService);
    }
}
