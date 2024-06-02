package fr.manuvai.tereroa.mocks;

import fr.manuvai.tereroa.models.Reservation;
import fr.manuvai.tereroa.models.Vehicle;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class VehicleMock {
    private VehicleMock() {
        throw new IllegalStateException("Cette classe est utilitaire et ne devrait pas être instanciée");
    }

    public static List<Vehicle> createVehicleList(OffsetDateTime... dates) {
        List<Vehicle> result = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        if (dates != null && dates.length > 0) {
            for (int i = 0; i < dates.length; i = i + 2) {
                Reservation reservation = new Reservation();
                reservation.setStartDate(new Date(dates[i].toInstant().toEpochMilli()));
                reservation.setEndDate(new Date(dates[i + 1].toInstant().toEpochMilli()));

                Vehicle vehicle = new Vehicle();
                vehicle.setName(formatter.format(dates[i]));

                Set<Reservation> reservationSet = vehicle.getReservationSet() == null
                        ? new HashSet<>()
                        : vehicle.getReservationSet();
                reservationSet.add(reservation);
                vehicle.setReservationSet(reservationSet);
                result.add(vehicle);
            }
        }

        return result;
    }
}
