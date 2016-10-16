package org.desu.volley.repository;

import org.desu.volley.domain.TrainingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the TrainingUser entity.
 */
@SuppressWarnings("unused")
public interface TrainingUserRepository extends JpaRepository<TrainingUser,Long> {

    @Query("select trainingUser from TrainingUser trainingUser where trainingUser.user.login = ?#{principal.username}")
    List<TrainingUser> findByUserIsCurrentUser();

}
