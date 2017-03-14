package org.desu.volley.security.social;

import org.desu.volley.config.JHipsterProperties;
import org.desu.volley.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class CustomSignInAdapter implements SignInAdapter {

    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(CustomSignInAdapter.class);

    @Inject
    private UserDetailsService userDetailsService;

    @Inject
    private JHipsterProperties jHipsterProperties;

    @Inject
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
        UserDetails user = userDetailsService.loadUserByUsername(userId);
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
            user,
            null,
            user.getAuthorities());
        Map<String, String> details = new HashMap<>();
        details.put("remoteAddress", ((ServletWebRequest) request).getRequest().getRemoteAddr());
        details.put("sessionId", request.getSessionId());
        details.put("socialUrl", connection.getProfileUrl());
        details.put("socialProvider", connection.getKey().getProviderId());
        newAuth.setDetails(details);
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        applicationEventPublisher.publishEvent(new AuthenticationSuccessEvent(newAuth));
        boolean hideMenu = (boolean) request.getAttribute("hideMenu", RequestAttributes.SCOPE_SESSION);
        String redirectAfterSignIn = jHipsterProperties.getSocial().getRedirectAfterSignIn();
        boolean isAdmin = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(AuthoritiesConstants.ADMIN::equals);
        if (isAdmin || !hideMenu) {
            return redirectAfterSignIn;
        }
        return redirectAfterSignIn + "?hideMenu=true";
    }
}
