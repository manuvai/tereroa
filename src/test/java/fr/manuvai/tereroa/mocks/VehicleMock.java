package fr.manuvai.tereroa.mocks;

import fr.manuvai.tereroa.models.Reservation;
import fr.manuvai.tereroa.models.Vehicle;

import java.time.LocalDate;
import java.util.*;

public class VehicleMock {
    private VehicleMock() {
        throw new IllegalStateException("Cette classe est utilitaire et ne devrait pas être instanciée");
    }

    public static List<Vehicle> createVehicleList(LocalDate... dates) {
        List<Vehicle> result = new ArrayList<>();

        if (dates != null && dates.length > 0) {
            for (int i = 0; i < dates.length; i = i + 2) {
                Reservation reservation = new Reservation();
                reservation.setStartDate(dates[i]);
                reservation.setEndDate(dates[i + 1]);

                Vehicle vehicle = new Vehicle();
                vehicle.setName(dates[i].toString());

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
