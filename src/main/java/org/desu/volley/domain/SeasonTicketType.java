package org.desu.volley.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.desu.volley.domain.enumeration.AttendingType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A SeasonTicketType.
 */
@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "season_ticket_type")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SeasonTicketType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Size(max = 100)
    @Column(name = "price", length = 100, nullable = false)
    private String price;

    @NotNull
    @Min(value = 1)
    @Column(name = "duration", nullable = false)
    private Integer duration;

    @NotNull
    @Min(value = 0)
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @NotNull
    @Min(value = 0)
    @Column(name = "index", nullable = false)
    private Integer index;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "attending_type", nullable = false)
    private AttendingType attendingType;

    @ManyToOne(optional = false)
    @NotNull
    private Organization organization;

    @Override
    public String toString() {
        return "SeasonTicketType{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", price='" + price + "'" +
            ", duration='" + duration + "'" +
            ", capacity='" + capacity + "'" +
            ", index='" + index + "'" +
            ", attendingType='" + attendingType + "'" +
            '}';
    }
}
