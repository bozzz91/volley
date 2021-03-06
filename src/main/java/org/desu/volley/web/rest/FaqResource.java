package org.desu.volley.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.desu.volley.domain.Faq;
import org.desu.volley.repository.FaqRepository;
import org.desu.volley.security.AuthoritiesConstants;
import org.desu.volley.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing Faq.
 */
@SuppressWarnings("UnnecessaryLocalVariable")
@RestController
@RequestMapping("/api")
public class FaqResource {

    private final Logger log = LoggerFactory.getLogger(FaqResource.class);

    @Inject
    private FaqRepository faqRepository;

    /**
     * POST  /faqs : Create a new faq.
     *
     * @param faq the faq to create
     * @return the ResponseEntity with status 201 (Created) and with body the new faq, or with status 400 (Bad Request) if the faq has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/faqs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Faq> createFaq(@Valid @RequestBody Faq faq) throws URISyntaxException {
        log.debug("REST request to save Faq : {}", faq);
        if (faq.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("faq", "idexists", "A new faq cannot already have an ID")).body(null);
        }
        Faq result = faqRepository.save(faq);
        return ResponseEntity.created(new URI("/api/faqs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("faq", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /faqs : Updates an existing faq.
     *
     * @param faq the faq to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated faq,
     * or with status 400 (Bad Request) if the faq is not valid,
     * or with status 500 (Internal Server Error) if the faq couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/faqs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Faq> updateFaq(@Valid @RequestBody Faq faq) throws URISyntaxException {
        log.debug("REST request to update Faq : {}", faq);
        if (faq.getId() == null) {
            return createFaq(faq);
        }
        Faq result = faqRepository.save(faq);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("faq", faq.getId().toString()))
            .body(result);
    }

    /**
     * GET  /faqs : get all the faqs.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of faqs in body
     */
    @RequestMapping(value = "/faqs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Faq> getAllFaqs() {
        log.debug("REST request to get all Faqs");
        List<Faq> faqs = faqRepository.findAll().stream()
            .sorted(Comparator.comparingInt(Faq::getIndex))
            .collect(Collectors.toList());
        return faqs;
    }

    /**
     * GET  /faqs/:id : get the "id" faq.
     *
     * @param id the id of the faq to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the faq, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/faqs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Faq> getFaq(@PathVariable Long id) {
        log.debug("REST request to get Faq : {}", id);
        Faq faq = faqRepository.findOne(id);
        return Optional.ofNullable(faq)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /faqs/:id : delete the "id" faq.
     *
     * @param id the id of the faq to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/faqs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteFaq(@PathVariable Long id) {
        log.debug("REST request to delete Faq : {}", id);
        faqRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("faq", id.toString())).build();
    }

}
