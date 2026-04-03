package fr.manuvai.tereroa.services;

import fr.manuvai.tereroa.exceptions.NotFoundException;
import fr.manuvai.tereroa.exceptions.TripDatesIncorrectException;
import fr.manuvai.tereroa.models.Reservation;
import fr.manuvai.tereroa.models.Vehicle;
import fr.manuvai.tereroa.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Set<Reservation> findReservationsByVehicleId(Long id) {
        return findById(id).getReservationSet();
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findAllAvailable(OffsetDateTime tripStartDate, OffsetDateTime tripEndDate) {
        if (tripStartDate == null ^ tripEndDate == null) {
            throw new TripDatesIncorrectException();
        }

        if (tripStartDate != null) {
            boolean isInvalid = tripEndDate.isBefore(tripStartDate)
                    || tripStartDate.toLocalDate().isBefore(LocalDate.now());
            if (isInvalid) {
                throw new TripDatesIncorrectException();
            }
        }

        List<Vehicle> vehicles = findAll();

        if (tripStartDate != null && tripEndDate != null) {
            LocalDate start = tripStartDate.toLocalDate();
            LocalDate end = tripEndDate.toLocalDate();

            vehicles = vehicles.stream()
                    .filter(vehicle -> isVehicleAvailable(vehicle, start, end))
                    .toList();
        }

        return vehicles;
    }

    private boolean isVehicleAvailable(Vehicle vehicle, LocalDate start, LocalDate end) {
        return vehicle != null
                && start != null
                && end != null
                && vehicle.getReservationSet()
                        .stream()
                        .noneMatch(reservation -> hasReservationCross(reservation, start, end));
    }

    private boolean hasReservationCross(Reservation reservation, LocalDate start, LocalDate end) {
        if (reservation == null) {
            return false;
        }

        LocalDate resStart = reservation.getStartDate();
        LocalDate resEnd = reservation.getEndDate();

        if (resStart == null || resEnd == null) {
            return false;
        }

        return !resEnd.isBefore(start) && !resStart.isAfter(end);
    }
}
