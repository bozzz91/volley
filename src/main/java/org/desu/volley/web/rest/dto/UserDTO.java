package org.desu.volley.web.rest.dto;

import org.desu.volley.config.Constants;
import org.desu.volley.domain.Authority;
import org.desu.volley.domain.City;
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

    private String imageUrl;

    @Email
    @Size(min = 5, max = 100)
    private String email;

    private boolean activated = false;

    private boolean readOnly = false;

    @Size(min = 2, max = 5)
    private String langKey;

    private Set<String> authorities;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this(user.getLogin(), user.getFirstName(), user.getLastName(), user.getEmail(),
            user.isActivated(), user.getLangKey(), user.getPhone(), user.getCity(),
            user.getImageUrl(), user.getId(), user.isReadOnly(),
            user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet()));
    }

    public UserDTO(String login, String firstName, String lastName,
                   String email, boolean activated, String langKey, String phone,
                   City city, String imageUrl, Long id, boolean readOnly, Set<String> authorities) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.activated = activated;
        this.langKey = langKey;
        this.authorities = authorities;
        this.phone = phone;
        this.city = city;
        this.imageUrl = imageUrl;
        this.id = id;
        this.readOnly = readOnly;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActivated() {
        return activated;
    }

    public String getLangKey() {
        return langKey;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
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
            ", city='" + city + '\'' +
            ", imageUrl='" + imageUrl + '\'' +
            ", activated=" + activated +
            ", readOnly=" + readOnly +
            ", langKey='" + langKey + '\'' +
            ", authorities=" + authorities +
            "}";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
