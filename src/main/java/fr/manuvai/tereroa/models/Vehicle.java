package fr.manuvai.tereroa.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
public class Vehicle {
    @Id
    private Long id;

    @Column
    private String name;

    @Column
    private Double pricePerDay;

    @ManyToOne
    private User owner;

    @OneToMany
    private Set<Reservation> reservationSet;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof final Vehicle vehicle)) return false;
        return Objects.equals(id, vehicle.id) && Objects.equals(name, vehicle.name)
                && Objects.equals(pricePerDay, vehicle.pricePerDay) && Objects.equals(owner, vehicle.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, pricePerDay, owner);
    }
}
