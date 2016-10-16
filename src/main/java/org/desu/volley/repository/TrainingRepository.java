package org.desu.volley.repository;

import org.desu.volley.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Training entity.
 */
@SuppressWarnings("unused")
public interface TrainingRepository extends JpaRepository<Training,Long> {

    @Query("select training from Training training where training.organizer.login = ?#{principal.username}")
    List<Training> findByOrganizerIsCurrentUser();

    @Query("select distinct training from Training training left join fetch training.trainingUsers")
    List<Training> findAllWithEagerRelationships();

    @Query("select training from Training training left join fetch training.trainingUsers where training.id =:id")
    Training findOneWithEagerRelationships(@Param("id") Long id);

}
