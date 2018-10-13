package org.desu.volley.service;

import org.desu.volley.domain.Organization;
import org.desu.volley.domain.SeasonTicketType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing SeasonTicketType.
 */
public interface SeasonTicketTypeService {

    /**
     * Save a seasonTicketType.
     * 
     * @param seasonTicketType the entity to save
     * @return the persisted entity
     */
    SeasonTicketType save(SeasonTicketType seasonTicketType);

    /**
     *  Get all the seasonTicketTypes.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<SeasonTicketType> findAll(Pageable pageable);

    /**
     *  Get all the seasonTicketTypes by organization.
     *
     *  @param organization the organization to find
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<SeasonTicketType> findByOrganization(Organization organization, Pageable pageable);

    /**
     *  Get the "id" seasonTicketType.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    SeasonTicketType findOne(Long id);

    /**
     *  Delete the "id" seasonTicketType.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);
}
