package fr.manuvai.tereroa.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "reservations")
@Getter
@Setter
public class Reservation {
    @Id
    private Long id;

    @Column
    private Date startDate;

    @Column
    private Date endDate;

    @ManyToOne
    private User customer;

    @ManyToOne
    private Vehicle vehicle;
}
