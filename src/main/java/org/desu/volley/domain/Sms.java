package org.desu.volley.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A Sms.
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
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

    @Column
    private String state;

    @ManyToMany
    @JoinTable(name = "sms_user",
        joinColumns = @JoinColumn(name="sms_id", referencedColumnName="id"),
        inverseJoinColumns = @JoinColumn(name="user_id", referencedColumnName="id"))
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<User> recipients = new HashSet<>();

    @Override
    public String toString() {
        return "Sms{" +
            "id=" + id +
            ", text='" + text + "'" +
            ", sendDate='" + sendDate + "'" +
            '}';
    }
}
