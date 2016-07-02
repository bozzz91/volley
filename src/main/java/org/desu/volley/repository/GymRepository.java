package org.desu.volley.repository;

import org.desu.volley.domain.Gym;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Gym entity.
 */
@SuppressWarnings("unused")
public interface GymRepository extends JpaRepository<Gym,Long> {

}
