package org.desu.volley.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.desu.volley.domain.Training;
import org.desu.volley.domain.User;
import org.desu.volley.repository.TrainingRepository;
import org.desu.volley.security.AuthoritiesConstants;
import org.desu.volley.service.UserService;
import org.desu.volley.web.rest.dto.UserDTO;
import org.desu.volley.web.rest.util.HeaderUtil;
import org.desu.volley.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for managing Training.
 */
@RestController
@RequestMapping("/api")
public class TrainingResource {

    private final Logger log = LoggerFactory.getLogger(TrainingResource.class);

    @Inject
    private TrainingRepository trainingRepository;

    @Inject
    private UserService userService;

    /**
     * POST  /trainings : Create a new training.
     *
     * @param training the training to create
     * @return the ResponseEntity with status 201 (Created) and with body the new training, or with status 400 (Bad Request) if the training has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/trainings",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Training> createTraining(@Valid @RequestBody Training training) throws URISyntaxException {
        log.debug("REST request to save Training : {}", training);
        if (training.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("training", "idexists", "A new training cannot already have an ID")).body(null);
        }
        Training result = trainingRepository.save(training);
        return ResponseEntity.created(new URI("/api/trainings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("training", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /trainings : Updates an existing training.
     *
     * @param training the training to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated training,
     * or with status 400 (Bad Request) if the training is not valid,
     * or with status 500 (Internal Server Error) if the training couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/trainings",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    public ResponseEntity<Training> updateTraining(@Valid @RequestBody Training training) throws URISyntaxException {
        log.debug("REST request to update Training : {}", training);
        if (training.getId() == null) {
            return createTraining(training);
        }
        Set<User> users = training.getUsers().stream()
            .map(u -> {
                if (u.getId() == null) {
                    return userService.getUserWithAuthoritiesByLogin(u.getLogin());
                }
                return Optional.of(u);
            })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
        training.setUsers(users);
        Training result = trainingRepository.save(training);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("training", training.getId().toString()))
            .body(result);
    }

    /**
     * GET  /trainings : get all the trainings.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of trainings in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/trainings",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Training>> getAllTrainings(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Trainings");
        Page<Training> page = trainingRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/trainings");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /trainings/:id : get the "id" training.
     *
     * @param id the id of the training to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the training, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/trainings/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Training> getTraining(@PathVariable Long id) {
        log.debug("REST request to get Training : {}", id);
        Training training = trainingRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(training)
            .map(result -> {
                Set<?> users = result.getUsers().stream()
                    .map(u -> userService.getUserWithAuthorities(u.getId()))
                    .map(UserDTO::new)
                    .collect(Collectors.toSet());
                //hack to get image urls which are transient in User
                result.setUsers((Set<User>) users);
                return new ResponseEntity<>(result, HttpStatus.OK);
            })
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /trainings/:id : delete the "id" training.
     *
     * @param id the id of the training to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/trainings/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteTraining(@PathVariable Long id) {
        log.debug("REST request to delete Training : {}", id);
        trainingRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("training", id.toString())).build();
    }

}
