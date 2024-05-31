package fr.manuvai.tereroa.services;

import fr.manuvai.tereroa.models.Reservation;
import fr.manuvai.tereroa.models.Vehicle;
import fr.manuvai.tereroa.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@Service
public class VehicleService {

    @Autowired
    VehicleRepository vehicleRepository;

    public List<Vehicle> findAllAvailable(OffsetDateTime start, OffsetDateTime end) {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        if (start != null && end != null) {
            vehicles = vehicles.stream()
                    .filter(vehicle -> isVehicleAvailable(vehicle, start, end))
                    .toList();
        }

        return vehicles;
    }

    private boolean isVehicleAvailable(Vehicle vehicle, OffsetDateTime start, OffsetDateTime end) {

        return vehicle != null
                && start != null
                && end != null
                && vehicle.getReservationSet()
                        .stream()
                        .noneMatch(reservation -> hasReservationCross(reservation, start, end));

    }

    private boolean hasReservationCross(Reservation reservation, OffsetDateTime start, OffsetDateTime end) {

        OffsetDateTime reservationStart = null;
        OffsetDateTime reservationEnd = null;

        if (reservation != null) {
            reservationStart = reservation.getStartDate()
                    .toInstant()
                    .atOffset(ZoneOffset.UTC);
            reservationEnd = reservation.getEndDate()
                    .toInstant()
                    .atOffset(ZoneOffset.UTC);
        }

        return reservation != null
                && (reservationEnd.isAfter(end) && reservationStart.isBefore(end)
                        || reservationStart.isBefore(start) && reservationEnd.isAfter(start));
    }

}
