package org.desu.volley.repository;

import org.desu.volley.domain.Sms;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Sms entity.
 */
@SuppressWarnings("unused")
public interface SmsRepository extends JpaRepository<Sms,Long> {

    @Query("select sms from Sms sms where sms.sender.login = ?#{principal.username}")
    List<Sms> findBySenderIsCurrentUser();

    @Query("select distinct sms from Sms sms")
    List<Sms> findAllWithEagerRelationships();

    @Query("select sms from Sms sms where sms.id =:id")
    Sms findOneWithEagerRelationships(@Param("id") Long id);

}
