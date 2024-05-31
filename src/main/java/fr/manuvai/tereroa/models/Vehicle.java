package fr.manuvai.tereroa.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
}
