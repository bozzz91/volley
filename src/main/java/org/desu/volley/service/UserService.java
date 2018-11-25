package org.desu.volley.service;

import org.desu.volley.domain.Authority;
import org.desu.volley.domain.City;
import org.desu.volley.domain.Organization;
import org.desu.volley.domain.User;
import org.desu.volley.repository.AuthorityRepository;
import org.desu.volley.repository.PersistentTokenRepository;
import org.desu.volley.repository.UserRepository;
import org.desu.volley.security.AuthoritiesConstants;
import org.desu.volley.security.SecurityUtils;
import org.desu.volley.service.util.RandomUtil;
import org.desu.volley.web.rest.dto.ManagedUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private SocialService socialService;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private UserRepository userRepository;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private UsersConnectionRepository usersConnectionRepository;

    @Inject
    private SessionRegistry sessionRegistry;

    public List<String> getLoggedInUsers() {
        final List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        final Collection<String> users = new HashSet<>();
        for (final Object principal : allPrincipals) {
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                final org.springframework.security.core.userdetails.User userPrincipal =
                    (org.springframework.security.core.userdetails.User) principal;

                String username = userPrincipal.getUsername();
                users.add(username);
            }
        }
        return new ArrayList<>(users);
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                userRepository.save(user);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);

        return userRepository.findOneByResetKey(key)
            .filter(user -> {
                ZonedDateTime oneDayAgo = ZonedDateTime.now().minusHours(24);
                return user.getResetDate().isAfter(oneDayAgo);
            })
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                userRepository.save(user);
                return user;
            });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmailIgnoreCase(mail)
            .filter(User::isActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(ZonedDateTime.now());
                userRepository.save(user);
                return user;
            });
    }

    public User createUserInformation(String login, String password, String firstName, String lastName, String email,
                                      String langKey, String phone, City city) {

        User newUser = new User();
        Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
        Set<Authority> authorities = new HashSet<>();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setCity(city);
        newUser.setLangKey(langKey);
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        authorities.add(authority);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User createUser(ManagedUserDTO managedUserDTO) {
        User user = new User();
        user.setLogin(managedUserDTO.getLogin());
        user.setFirstName(managedUserDTO.getFirstName());
        user.setLastName(managedUserDTO.getLastName());
        user.setEmail(managedUserDTO.getEmail());
        user.setPhone(managedUserDTO.getPhone());
        user.setCity(managedUserDTO.getCity());
        if (managedUserDTO.getLangKey() == null) {
            user.setLangKey("ru"); // default language
        } else {
            user.setLangKey(managedUserDTO.getLangKey());
        }
        if (managedUserDTO.getAuthorities() != null) {
            Set<Authority> authorities = new HashSet<>();
            boolean isSuperAdmin = SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.SUPER_ADMIN);
            managedUserDTO.getAuthorities().forEach(authority -> {
                if (!AuthoritiesConstants.ADMIN_ROLES.contains(authority) || isSuperAdmin)
                    authorities.add(authorityRepository.findOne(authority));
                }
            );
            user.setAuthorities(authorities);
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(ZonedDateTime.now());
        user.setActivated(true);
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    public void updateUserInformation(String firstName, String lastName, String email, String langKey, String phone,
                                      City city, Organization organization) {
        userRepository.findOneByLoginIgnoreCase(SecurityUtils.getCurrentUserLogin()).ifPresent(u -> {
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            u.setPhone(phone);
            u.setCity(city);
            u.setLangKey(langKey);
            if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
                u.setOrganization(organization);
            }
            userRepository.save(u);
            log.debug("Changed Information for User: {}", u);
        });
    }

    public void deleteUserInformation(String login) {
        userRepository.findOneByLoginIgnoreCase(login).ifPresent(u -> {
            socialService.deleteUserSocialConnection(u.getLogin());
            userRepository.delete(u);
            log.debug("Deleted User: {}", u);
        });
    }

    public void changePassword(String password) {
        userRepository.findOneByLoginIgnoreCase(SecurityUtils.getCurrentUserLogin()).ifPresent(u -> {
            String encryptedPassword = passwordEncoder.encode(password);
            u.setPassword(encryptedPassword);
            userRepository.save(u);
            log.debug("Changed password for User: {}", u);
        });
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        getLoggedInUsers();
        return userRepository.findOneByLoginIgnoreCase(login).map(u -> {
            loadImageUrl(u);
            eagerlyLoad(u);
            return u;
        });
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        User user = userRepository.findOneByLoginIgnoreCase(SecurityUtils.getCurrentUserLogin()).get();
        loadImageUrl(user);
        eagerlyLoad(user);
        return user;
    }

    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     * <p>
     * This is scheduled to get fired everyday, at midnight.
     * </p>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeOldPersistentTokens() {
        LocalDate now = LocalDate.now();
        persistentTokenRepository.findByTokenDateBefore(now.minusMonths(1)).forEach(token -> {
            log.debug("Deleting token {}", token.getSeries());
            User user = token.getUser();
            user.getPersistentTokens().remove(token);
            persistentTokenRepository.delete(token);
        });
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        ZonedDateTime now = ZonedDateTime.now();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusYears(3));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public User eagerlyLoad(User user) {
        user.getAuthorities().size(); // eagerly load the association
        City city = user.getCity(); //eagerly load city
        if (city != null) {
            city.getName();
        }
        if (user.getOrganization() != null) {
            user.getOrganization().getName();
        }
        return user;
    }

    void amendPersonalData(User user) {
        user.setEmail(null);
        user.setPhone(null);
        user.setCity(null);
        user.setResetDate(null);
        user.setResetKey(null);
        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN, AuthoritiesConstants.ORGANIZER)) {
            user.setLogin("login");
        }
    }

    public void loadImageUrl(User user) {
        ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(user.getLogin());
        MultiValueMap<String, Connection<?>> connections = connectionRepository.findAllConnections();
        connections.entrySet().stream()
            .filter(e -> !e.getValue().isEmpty())
            .map(Map.Entry::getValue)
            .flatMap(Collection::stream)
            .filter(connection -> connection.getImageUrl() != null)
            .findAny().ifPresent(connection -> user.setImageUrl(connection.getImageUrl()));
    }

    public Map<String, List<String>> getSocialProfiles(User user) {
        ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(user.getLogin());
        MultiValueMap<String, Connection<?>> connections = connectionRepository.findAllConnections();
        Map<String, List<String>> urls = new HashMap<>();
        connections.forEach((provider, connection) ->
            urls.put(provider, connection.stream().map(Connection::getProfileUrl).collect(Collectors.toList())
        ));
        return urls;
    }
}
