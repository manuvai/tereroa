package fr.manuvai.tereroa.services;

import fr.manuvai.tereroa.exceptions.NotFoundException;
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

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    public List<Vehicle> findAllAvailable(OffsetDateTime start, OffsetDateTime end) {
        List<Vehicle> vehicles = findAll();

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
        boolean isCrossingBefore = false;
        boolean isCrossingAfter = false;
        boolean isStartOrEndEqual = false;

        if (reservation != null) {
            OffsetDateTime reservationStart = reservation.getStartDate()
                    .toInstant()
                    .atOffset(ZoneOffset.UTC);
            OffsetDateTime reservationEnd = reservation.getEndDate()
                    .toInstant()
                    .atOffset(ZoneOffset.UTC);

            isCrossingBefore = reservationEnd.isAfter(end) && reservationStart.isBefore(end);
            isCrossingAfter = reservationStart.isBefore(start) && reservationEnd.isAfter(start);
            isStartOrEndEqual = reservationStart.isEqual(start) || reservationEnd.isEqual(end);
        }


        return isCrossingBefore || isCrossingAfter || isStartOrEndEqual;
    }

}
