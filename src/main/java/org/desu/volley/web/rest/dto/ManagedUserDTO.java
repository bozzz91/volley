package org.desu.volley.web.rest.dto;

import java.time.ZonedDateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.desu.volley.domain.City;
import org.desu.volley.domain.User;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A DTO extending the UserDTO, which is meant to be used in the user management UI.
 */
public class ManagedUserDTO extends UserDTO {

    public static final int PASSWORD_MIN_LENGTH = 4;
    public static final int PASSWORD_MAX_LENGTH = 100;

    private ZonedDateTime createdDate;

    private String lastModifiedBy;

    private Map<String, List<String>> socials;

    private ZonedDateTime lastModifiedDate;

    @NotNull
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    public ManagedUserDTO() {
    }

    public ManagedUserDTO(User user) {
        super(user);
        this.createdDate = user.getCreatedDate();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.password = null;
    }

    public ManagedUserDTO(Long id, String login, String password, String firstName, String lastName,
                          String email, boolean activated, String langKey, String phone, City city,
                          Set<String> authorities , ZonedDateTime createdDate, String lastModifiedBy,
                          ZonedDateTime lastModifiedDate, String imageUrl, boolean readOnly) {
        super(login, firstName, lastName, email, activated, langKey, phone, city, imageUrl, id, readOnly, authorities);
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
        this.password = password;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public ZonedDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "ManagedUserDTO{" +
            ", createdDate=" + createdDate +
            ", lastModifiedBy='" + lastModifiedBy + '\'' +
            ", lastModifiedDate=" + lastModifiedDate +
            "} " + super.toString();
    }

    public Map<String, List<String>> getSocials() {
        return socials;
    }

    public void setSocials(Map<String, List<String>> socials) {
        this.socials = socials;
    }
}
