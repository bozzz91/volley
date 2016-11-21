package org.desu.volley.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.desu.volley.domain.Training;
import org.desu.volley.domain.TrainingUser;
import org.desu.volley.domain.User;
import org.desu.volley.repository.TrainingRepository;
import org.desu.volley.repository.TrainingUserRepository;
import org.desu.volley.security.AuthoritiesConstants;
import org.desu.volley.service.SmsService;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing TrainingUser.
 */
@RestController
@RequestMapping("/api")
public class TrainingUserResource {

    private final Logger log = LoggerFactory.getLogger(TrainingUserResource.class);

    @Inject
    private TrainingUserRepository trainingUserRepository;

    @Inject
    private TrainingRepository trainingRepository;

    @Inject
    private SmsService smsService;

    @Inject
    private UserService userService;

    /**
     * POST  /training-users : Create a new trainingUser.
     *
     * @param trainingUser the trainingUser to create
     * @return the ResponseEntity with status 201 (Created) and with body the new trainingUser, or with status 400 (Bad Request) if the trainingUser has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/training-users",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.USER)
    @Transactional
    public ResponseEntity<TrainingUser> createTrainingUser(@RequestBody TrainingUser trainingUser) throws URISyntaxException {
        log.debug("REST request to save TrainingUser : {}", trainingUser);
        if (trainingUser.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("trainingUser", "idexists", "A new trainingUser cannot already have an ID")).body(null);
        }
        trainingUser.setRegisterDate(ZonedDateTime.now());
        List<TrainingUser> existed = trainingUserRepository.findByTrainingAndUser(trainingUser.getTraining(), trainingUser.getUser());
        TrainingUser result;
        if (existed.isEmpty()) {
            result = trainingUserRepository.save(trainingUser);
        } else {
            result = existed.get(0);
        }
        return ResponseEntity.created(new URI("/api/training-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("trainingUser", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /training-users : Updates an existing trainingUser.
     *
     * @param trainingUser the trainingUser to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated trainingUser,
     * or with status 400 (Bad Request) if the trainingUser is not valid,
     * or with status 500 (Internal Server Error) if the trainingUser couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/training-users",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TrainingUser> updateTrainingUser(@RequestBody TrainingUser trainingUser) throws URISyntaxException {
        log.debug("REST request to update TrainingUser : {}", trainingUser);
        if (trainingUser.getId() == null) {
            return createTrainingUser(trainingUser);
        }
        trainingUser.setRegisterDate(ZonedDateTime.now());
        TrainingUser result = trainingUserRepository.save(trainingUser);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("trainingUser", trainingUser.getId().toString()))
            .body(result);
    }

    /**
     * GET  /training-users : get all the trainingUsers.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of trainingUsers in body
     */
    @RequestMapping(value = "/training-users",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public List<TrainingUser> getAllTrainingUsers(Sort sort, @RequestParam(required = false, value = "trainingId") Long trainingId) {
        log.debug("REST request to get all TrainingUsers");
        List<TrainingUser> userList;
        if (trainingId == null) {
            userList = trainingUserRepository.findAll(sort);
        } else {
            Training t = new Training();
            t.setId(trainingId);
            userList = trainingUserRepository.findByTraining(t, sort);
        }
        userList.forEach(tu -> userService.loadImageUrl(tu.getUser()));
        return userList;
    }

    /**
     * GET  /training-users/:id : get the "id" trainingUser.
     *
     * @param id the id of the trainingUser to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the trainingUser, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/training-users/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<TrainingUser> getTrainingUser(@PathVariable Long id) {
        log.debug("REST request to get TrainingUser : {}", id);
        TrainingUser trainingUser = trainingUserRepository.findOne(id);
        return Optional.ofNullable(trainingUser)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /training-users/:id : delete the "id" trainingUser.
     *
     * @param id the id of the trainingUser to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/training-users/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<Void> deleteTrainingUser(@PathVariable Long id) {
        log.debug("REST request to delete TrainingUser : {}", id);
        TrainingUser trainingUser = trainingUserRepository.findOne(id);
        Training training = trainingRepository.findOneWithEagerRelationships(trainingUser.getTraining().getId());
        int limit = training.getLimit();
        List<User> users = new ArrayList<>(training.getUsers());
        int removedIndex = users.indexOf(trainingUser.getUser());

        trainingUserRepository.delete(id);

        if (removedIndex < limit && users.size() > limit) {
            User lastUser = users.get(limit);
            if (lastUser.getPhone() != null) {
                String msg = createSmsMessage(training, lastUser);
                smsService.sendSms(lastUser.getPhone(), msg);
            }
        }
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("trainingUser", id.toString())).build();
    }

    private String createSmsMessage(Training training, User lastUser) {
        String tz = lastUser.getCity().getTz();
        String msg = "Освободилось место на тренировку: "
            + training.getStartAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.of(tz)))
            + ", "
            + training.getGym().getLocation();
        return msg;
    }

}
