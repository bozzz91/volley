package org.desu.volley.repository;

import org.desu.volley.domain.Faq;

import org.springframework.data.jpa.repository.*;

/**
 * Spring Data JPA repository for the Faq entity.
 */
@SuppressWarnings("unused")
public interface FaqRepository extends JpaRepository<Faq,Long> {

}
