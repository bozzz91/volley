package org.desu.volley.security;

import java.util.Arrays;
import java.util.List;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String SUPER_ADMIN = "ROLE_SUPERADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final List<String> ADMIN_ROLES = Arrays.asList(ADMIN, SUPER_ADMIN);

    private AuthoritiesConstants() {
    }
}
