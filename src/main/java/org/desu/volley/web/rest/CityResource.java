package org.desu.volley.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.desu.volley.domain.City;
import org.desu.volley.repository.CityRepository;
import org.desu.volley.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing City.
 */
@RestController
@RequestMapping("/api")
public class CityResource {

    private final Logger log = LoggerFactory.getLogger(CityResource.class);
        
    @Inject
    private CityRepository cityRepository;
    
    /**
     * POST  /cities : Create a new city.
     *
     * @param city the city to create
     * @return the ResponseEntity with status 201 (Created) and with body the new city, or with status 400 (Bad Request) if the city has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/cities",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<City> createCity(@Valid @RequestBody City city) throws URISyntaxException {
        log.debug("REST request to save City : {}", city);
        if (city.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("city", "idexists", "A new city cannot already have an ID")).body(null);
        }
        City result = cityRepository.save(city);
        return ResponseEntity.created(new URI("/api/cities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("city", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /cities : Updates an existing city.
     *
     * @param city the city to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated city,
     * or with status 400 (Bad Request) if the city is not valid,
     * or with status 500 (Internal Server Error) if the city couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/cities",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<City> updateCity(@Valid @RequestBody City city) throws URISyntaxException {
        log.debug("REST request to update City : {}", city);
        if (city.getId() == null) {
            return createCity(city);
        }
        City result = cityRepository.save(city);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("city", city.getId().toString()))
            .body(result);
    }

    /**
     * GET  /cities : get all the cities.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of cities in body
     */
    @RequestMapping(value = "/cities",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<City> getAllCities() {
        log.debug("REST request to get all Cities");
        List<City> cities = cityRepository.findAll();
        return cities;
    }

    /**
     * GET  /cities/:id : get the "id" city.
     *
     * @param id the id of the city to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the city, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/cities/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<City> getCity(@PathVariable Long id) {
        log.debug("REST request to get City : {}", id);
        City city = cityRepository.findOne(id);
        return Optional.ofNullable(city)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /cities/:id : delete the "id" city.
     *
     * @param id the id of the city to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/cities/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        log.debug("REST request to delete City : {}", id);
        cityRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("city", id.toString())).build();
    }

}
