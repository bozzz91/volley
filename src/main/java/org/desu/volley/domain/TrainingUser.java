package org.desu.volley.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * A TrainingUser.
 */
@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id")
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
