package org.desu.volley.repository;

import org.desu.volley.domain.City;
import org.desu.volley.domain.User;

import java.time.ZonedDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(ZonedDateTime dateTime);

    List<User> findAllByCityAndPhoneIsNotNull(City city);

    Page<User> findAllByLoginIn(List<String> logins, Pageable pageable);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmailIgnoreCase(String email);

    Optional<User> findOneByPhoneIgnoreCase(String phone);

    Optional<User> findOneByLoginIgnoreCase(String login);

    Optional<User> findOneById(Long userId);

    @Override
    void delete(User t);

}
