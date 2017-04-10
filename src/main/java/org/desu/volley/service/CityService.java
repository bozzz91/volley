package org.desu.volley.service;

import org.desu.volley.domain.Authority;
import org.desu.volley.domain.City;
import org.desu.volley.repository.AuthorityRepository;
import org.desu.volley.repository.CityRepository;
import org.desu.volley.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for city operations.
 */
@Service
public class CityService {

    private final Logger log = LoggerFactory.getLogger(CityService.class);

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private CityRepository cityRepository;

    public void updateRole(City city) {
        Optional<Authority> authority = authorityRepository.findByName(city.getRole());
        if (!authority.isPresent()) {
            Authority newAuth = new Authority();
            newAuth.setName(AuthoritiesConstants.CITY_ADMIN_PREFIX + city.getRole());
            authorityRepository.save(newAuth);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void removeUnusedCityRoles() {
        List<Authority> authorities = authorityRepository.findAll().stream()
            .filter(authority -> authority.getName().startsWith(AuthoritiesConstants.CITY_ADMIN_PREFIX))
            .collect(Collectors.toList());
        List<City> cities = cityRepository.findAll();
        Set<Authority> usedRoles = cities.stream()
            .map(city -> AuthoritiesConstants.CITY_ADMIN_PREFIX + city.getRole())
            .map(Authority::new)
            .collect(Collectors.toSet());
        authorities.removeAll(usedRoles);
        if (!authorities.isEmpty()) {
            log.info("Found {} unused roles: {}", authorities.size(), authorities);
            authorityRepository.delete(authorities);
            log.info("Removed unused roles");
        }
    }
}
