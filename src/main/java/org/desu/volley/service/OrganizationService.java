package org.desu.volley.service;

import org.desu.volley.domain.Organization;
import org.desu.volley.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
@Transactional
public class OrganizationService {

    @Inject
    private OrganizationRepository organizationRepository;

    @Transactional(readOnly = true)
    public Organization loadWithUsers(Long id) {
        Organization organization = organizationRepository.findOne(id);
        if (organization != null) {
            organization.setOrganizers(organization.getOrganizers());
        }
        return organization;
    }
}
