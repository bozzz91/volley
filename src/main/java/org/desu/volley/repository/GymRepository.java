package org.desu.volley.repository;

import org.desu.volley.domain.Gym;
import org.desu.volley.domain.Organization;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Gym entity.
 */
public interface GymRepository extends JpaRepository<Gym,Long> {

    List<Gym> findByOrganization(Organization organization, Sort pageable);

}
