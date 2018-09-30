package org.desu.volley.repository;

import org.desu.volley.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Organization entity.
 */
@SuppressWarnings("unused")
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

}
