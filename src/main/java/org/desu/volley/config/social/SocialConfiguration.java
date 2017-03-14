package org.desu.volley.config.social;

import org.desu.volley.repository.CustomSocialUsersConnectionRepository;
import org.desu.volley.repository.SocialUserConnectionRepository;
import org.desu.volley.security.social.CustomSignInAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.connect.web.ProviderSignInInterceptor;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.social.vkontakte.connect.VKontakteConnectionFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.inject.Inject;

// jhipster-needle-add-social-connection-factory-import-package

/**
 * Basic Spring Social configuration.
 *
 * <p>Creates the beans necessary to manage Connections to social services and
 * link accounts from those services to internal Users.</p>
 */
@Configuration
@EnableSocial
public class SocialConfiguration implements SocialConfigurer {
    private final Logger log = LoggerFactory.getLogger(SocialConfiguration.class);

    @Inject
    private SocialUserConnectionRepository socialUserConnectionRepository;

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        // Google configuration
        String googleClientId = environment.getProperty("spring.social.google.clientId");
        String googleClientSecret = environment.getProperty("spring.social.google.clientSecret");
        if (googleClientId != null && googleClientSecret != null) {
            log.debug("Configuring GoogleConnectionFactory");
            connectionFactoryConfigurer.addConnectionFactory(
                new GoogleConnectionFactory(
                    googleClientId,
                    googleClientSecret
                )
            );
        } else {
            log.error("Cannot configure GoogleConnectionFactory id or secret null");
        }

        // Facebook configuration
        String facebookClientId = environment.getProperty("spring.social.facebook.clientId");
        String facebookClientSecret = environment.getProperty("spring.social.facebook.clientSecret");
        if (facebookClientId != null && facebookClientSecret != null) {
            log.debug("Configuring FacebookConnectionFactory");
            connectionFactoryConfigurer.addConnectionFactory(
                new FacebookConnectionFactory(
                    facebookClientId,
                    facebookClientSecret
                )
            );
        } else {
            log.error("Cannot configure FacebookConnectionFactory id or secret null");
        }

        // Twitter configuration
        String twitterClientId = environment.getProperty("spring.social.twitter.clientId");
        String twitterClientSecret = environment.getProperty("spring.social.twitter.clientSecret");
        if (twitterClientId != null && twitterClientSecret != null) {
            log.debug("Configuring TwitterConnectionFactory");
            connectionFactoryConfigurer.addConnectionFactory(
                new TwitterConnectionFactory(
                    twitterClientId,
                    twitterClientSecret
                )
            );
        } else {
            log.error("Cannot configure TwitterConnectionFactory id or secret null");
        }

        // VK configuration
        String vkClientId = environment.getProperty("spring.social.vk.clientId");
        String vkClientSecret = environment.getProperty("spring.social.vk.clientSecret");
        if (vkClientId != null && vkClientSecret != null) {
            log.debug("Configuring VKConnectionFactory");
            connectionFactoryConfigurer.addConnectionFactory(
                new VKontakteConnectionFactory(
                    vkClientId,
                    vkClientSecret
                )
            );
        } else {
            log.error("Cannot configure VKConnectionFactory id or secret null");
        }

        // jhipster-needle-add-social-connection-factory
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        return new CustomSocialUsersConnectionRepository(socialUserConnectionRepository, connectionFactoryLocator);
    }

    @Bean
    public SignInAdapter signInAdapter() {
        return new CustomSignInAdapter();
    }

    @Bean
    public ProviderSignInController providerSignInController(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository, SignInAdapter signInAdapter) throws Exception {
        ProviderSignInController providerSignInController = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, signInAdapter);
        providerSignInController.setSignUpUrl("/social/signup");

        providerSignInController.addSignInInterceptor(new ProviderSignInInterceptorImpl<VKontakte>() {});
        providerSignInController.addSignInInterceptor(new ProviderSignInInterceptorImpl<Google>() {});
        providerSignInController.addSignInInterceptor(new ProviderSignInInterceptorImpl<Facebook>() {});

        return providerSignInController;
    }

    @Bean
    public ProviderSignInUtils getProviderSignInUtils(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository) {
        return new ProviderSignInUtils(connectionFactoryLocator, usersConnectionRepository);
    }

    interface ProviderSignInInterceptorImpl<T> extends ProviderSignInInterceptor<T> {

        @Override
        default void preSignIn(ConnectionFactory<T> connectionFactory, MultiValueMap<String, String> multiValueMap, WebRequest webRequest) {
            String hideMenu = webRequest.getParameter("hideMenu");
            webRequest.setAttribute("hideMenu", "true".equalsIgnoreCase(hideMenu), RequestAttributes.SCOPE_SESSION);
        }

        @Override
        default void postSignIn(Connection<T> connection, WebRequest webRequest) {

        }
    }
}
