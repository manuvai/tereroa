package fr.manuvai.tereroa.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "reservations")
@Getter
@Setter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Date startDate;

    @Column
    private Date endDate;

    @ManyToOne
    private User customer;

    @ManyToOne
    private Vehicle vehicle;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof final Reservation that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(startDate, that.startDate)
                && Objects.equals(endDate, that.endDate) && Objects.equals(customer, that.customer)
                && Objects.equals(vehicle, that.vehicle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startDate, endDate, customer, vehicle);
    }
}
