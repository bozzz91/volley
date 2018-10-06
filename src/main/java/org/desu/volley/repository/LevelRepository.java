package org.desu.volley.repository;

import org.desu.volley.domain.Level;
import org.desu.volley.domain.Organization;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Level entity.
 */
public interface LevelRepository extends JpaRepository<Level,Long> {
    List<Level> findByOrganization(Organization organization, Sort pageable);
}
