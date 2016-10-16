package org.desu.volley.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A TrainingUser.
 */
@Entity
@Table(name = "training_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TrainingUser implements Serializable, Comparable<TrainingUser> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "register_date", nullable = false)
    private ZonedDateTime registerDate;

    @JsonBackReference("training")
    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
//    @NotFound(action = NotFoundAction.IGNORE)
    private Training training;

    @ManyToOne(optional = false)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(ZonedDateTime registerDate) {
        this.registerDate = registerDate;
    }

    public Training getTraining() {
        return training;
    }

    public void setTraining(Training training) {
        this.training = training;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TrainingUser trainingUser = (TrainingUser) o;
        if(trainingUser.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, trainingUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TrainingUser{" +
            "id=" + id +
            ", registerDate='" + registerDate + "'" +
            '}';
    }

    @Override
    public int compareTo(TrainingUser o) {
        return this.registerDate.compareTo(o.registerDate);
    }
}
