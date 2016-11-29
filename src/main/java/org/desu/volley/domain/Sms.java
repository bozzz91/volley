package org.desu.volley.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Sms.
 */
@Entity
@Table(name = "sms")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Sms implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "text", length = 100, nullable = false)
    private String text;

    @NotNull
    @Column(name = "send_date", nullable = false)
    private ZonedDateTime sendDate;

    @ManyToOne
    @NotNull
    private User sender;

    @ManyToMany
    @JoinTable(name = "sms_user")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<User> recipients = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ZonedDateTime getSendDate() {
        return sendDate;
    }

    public void setSendDate(ZonedDateTime sendDate) {
        this.sendDate = sendDate;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User user) {
        this.sender = user;
    }

    public Set<User> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<User> users) {
        this.recipients = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sms sms = (Sms) o;
        if(sms.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, sms.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Sms{" +
            "id=" + id +
            ", text='" + text + "'" +
            ", sendDate='" + sendDate + "'" +
            '}';
    }
}
