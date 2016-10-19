package org.desu.volley.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A Training.
 */
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
    @Min(value = 0)
    @Column(name = "price", nullable = false)
    private Integer price = 150;

    @NotNull
    @Min(value = 0)
    @Column(name = "user_limit", nullable = false)
    private Integer limit = 18;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private TrainingState state = TrainingState.REGISTRATION;

    @Column(name = "description")
    private String description;

    @JsonManagedReference("training")
    @OneToMany(mappedBy = "training", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<TrainingUser> trainingUsers = new HashSet<>();

    @ManyToOne(optional = false)
    private Level level;

    @ManyToOne(optional = false)
    private User organizer;

    @ManyToOne(optional = false)
    private Gym gym;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(ZonedDateTime startAt) {
        this.startAt = startAt;
    }

    public ZonedDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(ZonedDateTime endAt) {
        this.endAt = endAt;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public TrainingState getState() {
        return state;
    }

    public void setState(TrainingState state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Transient
    public List<User> getUsers() {
        return trainingUsers.stream().sorted().map(TrainingUser::getUser).collect(Collectors.toList());
    }

    public Set<TrainingUser> getTrainingUsers() {
        return trainingUsers;
    }

    public void setTrainingUsers(Set<TrainingUser> users) {
        this.trainingUsers = users;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User user) {
        this.organizer = user;
    }

    public Gym getGym() {
        return gym;
    }

    public void setGym(Gym gym) {
        this.gym = gym;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Training training = (Training) o;
        if(training.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, training.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Training{" +
            "id=" + id +
            ", startAt='" + startAt + "'" +
            ", endAt='" + endAt + "'" +
            ", price='" + price + "'" +
            ", state='" + state + "'" +
            ", description='" + description + "'" +
            '}';
    }
}
