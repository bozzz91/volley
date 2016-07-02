package org.desu.volley.repository;

import org.desu.volley.domain.Training;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Training entity.
 */
@SuppressWarnings("unused")
public interface TrainingRepository extends JpaRepository<Training,Long> {

    @Query("select training from Training training where training.organizer.login = ?#{principal.username}")
    List<Training> findByOrganizerIsCurrentUser();

    @Query("select distinct training from Training training left join fetch training.users")
    List<Training> findAllWithEagerRelationships();

    @Query("select training from Training training left join fetch training.users where training.id =:id")
    Training findOneWithEagerRelationships(@Param("id") Long id);

}
