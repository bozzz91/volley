package org.desu.volley.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.desu.volley.domain.enumeration.TrainingState;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A Training.
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "training")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Training implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "start_at", nullable = false)
    private ZonedDateTime startAt;

    @NotNull
    @Column(name = "end_at", nullable = false)
    private ZonedDateTime endAt;

    @NotNull
    @Column(name = "price", nullable = false)
    private String price = "150";

    @NotNull
    @Min(value = 0)
    @Column(name = "user_limit", nullable = false)
    private Integer limit = 18;

    @Column(name = "booking")
    private String booking;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private TrainingState state = TrainingState.REGISTRATION;

    @Column(name = "description")
    private String description;

    @JsonManagedReference("training")
    @OneToMany(mappedBy = "training", fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<TrainingUser> trainingUsers = new HashSet<>();

    @ManyToOne(optional = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private Level level;

    @ManyToOne(optional = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private User organizer;

    @ManyToOne(optional = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private Gym gym;

    @Transient
    public List<User> getUsers() {
        return trainingUsers.stream().sorted().map(TrainingUser::getUser).collect(Collectors.toList());
    }

    public Integer getBookingCount() {
        return booking == null ? 0 : booking.split(",").length;
    }

    @Override
    public String toString() {
        return "Training{" +
            "id=" + id +
            ", startAt='" + startAt + "'" +
            ", endAt='" + endAt + "'" +
            ", price='" + price + "'" +
            ", state='" + state + "'" +
            ", booking='" + booking + "'" +
            ", limit='" + limit + "'" +
            ", description='" + description + "'" +
            '}';
    }
}
