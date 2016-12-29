package org.desu.volley.repository;

import org.desu.volley.domain.Gym;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Gym entity.
 */
@SuppressWarnings("unused")
public interface GymRepository extends JpaRepository<Gym,Long> {

    @Query("select gym from Gym gym where gym.id =:id")
    Gym findWithEagerCity(@Param("id") Long id);

}
