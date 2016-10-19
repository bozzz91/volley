package org.desu.volley.service;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

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
        connectionRepository.findAllConnections().keySet().stream()
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
        log.info("userProfile: email {}, username {}, name {}", userProfile.getEmail(), userProfile.getUsername(), userProfile.getName());
        String providerId = connection.getKey().getProviderId();
        User user = createUserIfNotExist(userProfile, langKey, providerId);
        createSocialConnection(user.getLogin(), connection);
        mailService.sendSocialRegistrationValidationEmail(user, providerId);
    }

    private User createUserIfNotExist(UserProfile userProfile, String langKey, String providerId) {
        String email = userProfile.getEmail();
        String userName = userProfile.getUsername();
        String name = userProfile.getName();
        if (!StringUtils.isBlank(userName)) {
            userName = userName.toLowerCase(Locale.ENGLISH);
        }
        if (!StringUtils.isBlank(name)) {
            name = name.toLowerCase(Locale.ENGLISH).replace(" ", "_");
        }
        if (StringUtils.isBlank(email) && StringUtils.isBlank(userName)&& StringUtils.isBlank(name)) {
            log.error("Cannot create social user because email and login and name are null");
            throw new IllegalArgumentException("Email and login and name cannot be null");
        }
        if (StringUtils.isBlank(userName)) {
            userName = name;
        }
        if (StringUtils.isBlank(email) && userRepository.findOneByLogin(userName).isPresent()) {
            log.error("Cannot create social user because email is null and login already exist, login -> {}", userName);
            throw new IllegalArgumentException("Email cannot be null with an existing login");
        }
        if (!StringUtils.isBlank(email)) {
            Optional<User> user = userRepository.findOneByEmail(email);
            if (user.isPresent()) {
                log.info("User already exist associate the connection to this account");
                return user.get();
            }
        }

        String login = getLoginDependingOnProviderId(userProfile, providerId);
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
        switch (providerId) {
            case "twitter":
                return "tw"+userProfile.getUsername().toLowerCase();
            default:
                return extractLoginForProvider(userProfile, providerId);
        }
    }

    private String extractLoginForProvider(UserProfile userProfile, String provider) {
        return userProfile.getEmail() != null
            ? userProfile.getEmail()
            : userProfile.getUsername() != null
            ? (provider + userProfile.getUsername())
            : userProfile.getName() != null
            ? (provider + userProfile.getName()).toLowerCase(Locale.US).replace(" ", "_")
            : userProfile.getId() != null
            ? (provider + userProfile.getId())
            : null;
    }

    private void createSocialConnection(String login, Connection<?> connection) {
        ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(login);
        connectionRepository.addConnection(connection);
    }
}
