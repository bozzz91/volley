package org.desu.volley.service;

import org.apache.commons.lang.RandomStringUtils;
import org.desu.volley.domain.Authority;
import org.desu.volley.domain.User;
import org.desu.volley.repository.AuthorityRepository;
import org.desu.volley.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.*;

@Service
public class SocialService {
    private final Logger log = LoggerFactory.getLogger(SocialService.class);

    @Inject
    private UsersConnectionRepository usersConnectionRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private UserRepository userRepository;

    @Inject
    private MailService mailService;

    public void deleteUserSocialConnection(String login) {
        ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(login);
        connectionRepository.findAllConnections().keySet()
            .forEach(providerId -> {
                connectionRepository.removeConnections(providerId);
                log.debug("Delete user social connection providerId: {}", providerId);
            });
    }

    public void createSocialUser(Connection<?> connection, String langKey) {
        if (connection == null) {
            log.error("Cannot create social user because connection is null");
            throw new IllegalArgumentException("Connection cannot be null");
        }
        UserProfile userProfile = connection.fetchUserProfile();
        log.info("userProfile: email {}, id {}, username {}", userProfile.getEmail(), userProfile.getId(), userProfile.getUsername());
        String providerId = connection.getKey().getProviderId();
        User user = createUserIfNotExist(userProfile, langKey, providerId);
        createSocialConnection(user.getLogin(), connection);
        mailService.sendSocialRegistrationValidationEmail(user, providerId);
    }

    private User createUserIfNotExist(UserProfile userProfile, String langKey, String providerId) {
        String email = userProfile.getEmail();
        String login = getLoginDependingOnProviderId(userProfile, providerId);

        if (isBlank(login)) {
            log.error("Cannot create social user because email and id and username are null");
            throw new IllegalArgumentException("Email and id and username can not be null");
        }
        if (!isBlank(email)) {
            Optional<User> user = userRepository.findOneByEmailIgnoreCase(email);
            if (user.isPresent()) {
                log.info("User already exist associate the connection to this account by email: {}", email);
                return user.get();
            }
        }
        Optional<User> user = userRepository.findOneByLoginIgnoreCase(login);
        if (user.isPresent()) {
            log.error("User already exist associate the connection to this account by login: {}", login);
            return user.get();
        }

        String encryptedPassword = passwordEncoder.encode(RandomStringUtils.random(10));
        Set<Authority> authorities = new HashSet<>(1);
        authorities.add(authorityRepository.findOne("ROLE_USER"));

        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userProfile.getFirstName());
        newUser.setLastName(userProfile.getLastName());
        newUser.setEmail(email);
        newUser.setActivated(true);
        newUser.setAuthorities(authorities);
        newUser.setLangKey(langKey);

        return userRepository.save(newUser);
    }

    /**
     * @return login if provider manage a login like Twitter or Github otherwise email address.
     *         Because provider like Google or Facebook didn't provide login or login like "12099388847393"
     */
    private String getLoginDependingOnProviderId(UserProfile userProfile, String providerId) {
        return userProfile.getEmail() != null
            ? userProfile.getEmail()
            : userProfile.getId() != null
            ? (providerId + "-" + userProfile.getId())
            : userProfile.getUsername() != null
            ? (providerId + "-" + userProfile.getUsername())
            : null;
    }

    private void createSocialConnection(String login, Connection<?> connection) {
        ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(login);
        connectionRepository.addConnection(connection);
    }
}
