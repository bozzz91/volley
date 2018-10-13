package org.desu.volley.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.desu.volley.domain.Organization;
import org.desu.volley.domain.SeasonTicketType;
import org.desu.volley.repository.SeasonTicketTypeRepository;
import org.desu.volley.service.SeasonTicketTypeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Service Implementation for managing SeasonTicketType.
 */
@Slf4j
@Service
@Transactional
public class SeasonTicketTypeServiceImpl implements SeasonTicketTypeService{

    @Inject
    private SeasonTicketTypeRepository seasonTicketTypeRepository;
    
    /**
     * Save a seasonTicketType.
     * 
     * @param seasonTicketType the entity to save
     * @return the persisted entity
     */
    @Override
    public SeasonTicketType save(SeasonTicketType seasonTicketType) {
        log.debug("Request to save SeasonTicketType : {}", seasonTicketType);
        SeasonTicketType result = seasonTicketTypeRepository.save(seasonTicketType);
        return result;
    }

    /**
     *  Get all the seasonTicketTypes.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true) 
    public Page<SeasonTicketType> findAll(Pageable pageable) {
        log.debug("Request to get all SeasonTicketTypes");
        Page<SeasonTicketType> result = seasonTicketTypeRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get all the seasonTicketTypes by organization.
     *
     *  @param organization the organization to find
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SeasonTicketType> findByOrganization(Organization organization, Pageable pageable) {
        log.debug("Request to get all SeasonTicketTypes by organization");
        Page<SeasonTicketType> result = seasonTicketTypeRepository.findByOrganization(organization, pageable);
        return result;
    }

    /**
     *  Get one seasonTicketType by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true) 
    public SeasonTicketType findOne(Long id) {
        log.debug("Request to get SeasonTicketType : {}", id);
        SeasonTicketType seasonTicketType = seasonTicketTypeRepository.findOne(id);
        return seasonTicketType;
    }

    /**
     *  Delete the  seasonTicketType by id.
     *  
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete SeasonTicketType : {}", id);
        seasonTicketTypeRepository.delete(id);
    }
}
