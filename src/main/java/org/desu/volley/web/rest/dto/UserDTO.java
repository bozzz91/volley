package org.desu.volley.web.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.desu.volley.config.Constants;
import org.desu.volley.domain.Authority;
import org.desu.volley.domain.City;
import org.desu.volley.domain.Organization;
import org.desu.volley.domain.User;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with his authorities.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    private Long id;

    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 100)
    private String login;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Size(min = 12, max = 12)
    @Pattern(regexp = Constants.PHONE_REGEX)
    private String phone;

    private City city;

    private Organization organization;

    private String imageUrl;

    @Email
    @Size(min = 5, max = 100)
    private String email;

    private boolean activated = false;

    private boolean readOnly = false;

    @Size(min = 2, max = 5)
    private String langKey;

    private Set<String> authorities;

    public UserDTO(User user) {
        this(user.getLogin(), user.getFirstName(), user.getLastName(), user.getEmail(),
            user.isActivated(), user.getLangKey(), user.getPhone(), user.getCity(),
            user.getImageUrl(), user.getId(), user.isReadOnly(), user.getOrganization(),
            user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet()));
    }

    public UserDTO(String login, String firstName, String lastName,
                   String email, boolean activated, String langKey, String phone,
                   City city, String imageUrl, Long id, boolean readOnly,
                   Organization organization, Set<String> authorities) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.activated = activated;
        this.langKey = langKey;
        this.authorities = authorities;
        this.phone = phone;
        this.city = city;
        this.organization = organization;
        this.imageUrl = imageUrl;
        this.id = id;
        this.readOnly = readOnly;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
            "id='" + id + '\'' +
            ", login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", phone='" + phone + '\'' +
            ", city='" + city.getName() + '\'' +
            ", imageUrl='" + imageUrl + '\'' +
            ", activated=" + activated +
            ", readOnly=" + readOnly +
            ", langKey='" + langKey + '\'' +
            ", authorities=" + authorities +
            "}";
    }
}
