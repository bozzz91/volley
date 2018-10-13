package org.desu.volley.repository;

import org.desu.volley.domain.Organization;
import org.desu.volley.domain.SeasonTicketType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the SeasonTicketType entity.
 */
public interface SeasonTicketTypeRepository extends JpaRepository<SeasonTicketType,Long> {
    Page<SeasonTicketType> findByOrganization(Organization organization, Pageable pageable);
}
