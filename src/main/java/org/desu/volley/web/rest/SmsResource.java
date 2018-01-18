package org.desu.volley.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.desu.volley.domain.Sms;
import org.desu.volley.domain.User;
import org.desu.volley.repository.UserRepository;
import org.desu.volley.security.AuthoritiesConstants;
import org.desu.volley.service.SmsService;
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
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Sms.
 */
@RestController
@RequestMapping("/api")
@Secured(AuthoritiesConstants.USER)
public class SmsResource {

    private final Logger log = LoggerFactory.getLogger(SmsResource.class);

    @Inject
    private SmsService smsService;
    @Inject
    private UserRepository userRepository;

    /**
     * POST  /sms : Create a new sms.
     *
     * @param sms the sms to create
     * @return the ResponseEntity with status 201 (Created) and with body the new sms, or with status 400 (Bad Request) if the sms has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sms",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sms> createSms(@Valid @RequestBody Sms sms) throws URISyntaxException {
        log.debug("REST request to save Sms : {}", sms);
        if (sms.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sms", "idexists", "A new sms cannot already have an ID")).body(null);
        }
        if (sms.getRecipients().isEmpty()) {
            List<User> cityUsers = userRepository.findAllByCityAndPhoneIsNotNull(sms.getSender().getCity());
            sms.setRecipients(new HashSet<>(cityUsers));
        }
        Sms result = smsService.save(sms, true);
        return ResponseEntity.created(new URI("/api/sms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("sms", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sms : Updates an existing sms.
     *
     * @param sms the sms to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated sms,
     * or with status 400 (Bad Request) if the sms is not valid,
     * or with status 500 (Internal Server Error) if the sms couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sms",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sms> updateSms(@Valid @RequestBody Sms sms) throws URISyntaxException {
        log.debug("REST request to update Sms : {}", sms);
        if (sms.getId() == null) {
            return createSms(sms);
        }
        Sms result = smsService.save(sms, false);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("sms", sms.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sms : get all the sms.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of sms in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/sms",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Sms>> getAllSms(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Sms");
        Page<Sms> page = smsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sms");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /sms/:id : get the "id" sms.
     *
     * @param id the id of the sms to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the sms, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/sms/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sms> getSms(@PathVariable Long id) {
        log.debug("REST request to get Sms : {}", id);
        Sms sms = smsService.findOne(id);
        return Optional.ofNullable(sms)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /sms/:id : delete the "id" sms.
     *
     * @param id the id of the sms to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/sms/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.SUPER_ADMIN)
    public ResponseEntity<Void> deleteSms(@PathVariable Long id) {
        log.debug("REST request to delete Sms : {}", id);
        smsService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("sms", id.toString())).build();
    }

}
