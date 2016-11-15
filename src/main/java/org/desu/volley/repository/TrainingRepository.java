package org.desu.volley.repository;

import org.desu.volley.domain.City;
import org.desu.volley.domain.Training;
import org.desu.volley.domain.enumeration.TrainingState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Training> findByOrganizerIsCurrentUser(Pageable pageable);

    @Query("select distinct training from Training training left join fetch training.trainingUsers")
    List<Training> findAllWithEagerRelationships();

    @Query("select training from Training training left join fetch training.trainingUsers where training.id =:id")
    Training findOneWithEagerRelationships(@Param("id") Long id);

    List<Training> findByStateNot(TrainingState state);

    @Query("select training from Training training where training.gym.city =:city and training.state in :states")
    Page<Training> findByCityAndStates(@Param("city") City city, @Param("states") List<TrainingState> states, Pageable pageable);

    @Query("select training from Training training where training.gym.city =:city")
    Page<Training> findByCity(@Param("city") City city, Pageable pageable);
}
