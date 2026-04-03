package fr.manuvai.tereroa.services;

import fr.manuvai.tereroa.exceptions.NotFoundException;
import fr.manuvai.tereroa.exceptions.TripDatesIncorrectException;
import fr.manuvai.tereroa.mocks.VehicleMock;
import fr.manuvai.tereroa.models.Reservation;
import fr.manuvai.tereroa.models.Vehicle;
import fr.manuvai.tereroa.repositories.VehicleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {
    @InjectMocks
    VehicleService vehicleServiceMock;

    @Mock
    VehicleRepository vehicleRepositoryMock;

    // ========== findById tests ==========

    @Test
    void testFindById_Found() {
        // GIVEN
        Long vehicleId = 1L;
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setName("TestCar");

        Mockito.doReturn(Optional.of(vehicle))
                .when(vehicleRepositoryMock)
                .findById(vehicleId);

        // WHEN
        Vehicle result = vehicleServiceMock.findById(vehicleId);

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertEquals(vehicleId, result.getId());
        Assertions.assertEquals("TestCar", result.getName());
    }

    @Test
    void testFindById_NotFound_ThrowsNotFoundException() {
        // GIVEN
        Long vehicleId = 999L;

        Mockito.doReturn(Optional.empty())
                .when(vehicleRepositoryMock)
                .findById(vehicleId);

        // WHEN / THEN
        Assertions.assertThrows(NotFoundException.class,
                () -> vehicleServiceMock.findById(vehicleId));
    }

    // ========== findAllAvailable - null dates ==========

    @Test
    void testFindAllAvailable_DatesNull_Ok() {
        // GIVEN
        Mockito.doReturn(Collections.emptyList())
                .when(vehicleRepositoryMock)
                .findAll();

        // WHEN
        List<Vehicle> responseList = vehicleServiceMock.findAllAvailable(null, null);

        // THEN
        Assertions.assertTrue(responseList != null && responseList.isEmpty());
    }

    @Test
    void testFindAllAvailable_DatesNotNull_Ok() {
        // GIVEN - use future dates so validation passes
        LocalDate now = LocalDate.now();
        LocalDate[] repositoryDates = {
                now.plusDays(10),
                now.plusDays(15),
                now.plusDays(50),
                now.plusDays(55),
                now.plusDays(100),
                now.plusDays(105),
        };
        List<Vehicle> repositoryResult = VehicleMock.createVehicleList(repositoryDates);
        LocalDate[] expectedDates = {
                now.plusDays(50),
                now.plusDays(55),
                now.plusDays(100),
                now.plusDays(105),
        };
        List<Vehicle> expectedResult = VehicleMock.createVehicleList(expectedDates);

        // Query with dates matching the first reservation period
        OffsetDateTime queryStart = repositoryDates[0].atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime queryEnd = repositoryDates[1].atStartOfDay().atOffset(ZoneOffset.UTC);

        // WHEN
        Mockito.doReturn(repositoryResult)
                .when(vehicleRepositoryMock)
                .findAll();

        List<Vehicle> responseList = vehicleServiceMock.findAllAvailable(queryStart, queryEnd);

        // THEN
        Assertions.assertEquals(expectedResult.size(), responseList.size());
    }

    // ========== findAllAvailable - overlap scenarios ==========

    @Test
    void testFindAllAvailable_ReservationFullyContainsRequestedPeriod_VehicleExcluded() {
        // GIVEN - reservation [day 5, day 20], query [day 10, day 15]
        // The reservation fully contains the requested period
        LocalDate now = LocalDate.now();
        Vehicle vehicle = createVehicleWithReservation(now.plusDays(5), now.plusDays(20));

        Mockito.doReturn(List.of(vehicle))
                .when(vehicleRepositoryMock)
                .findAll();

        OffsetDateTime queryStart = toOffsetDateTime(now.plusDays(10));
        OffsetDateTime queryEnd = toOffsetDateTime(now.plusDays(15));

        // WHEN
        List<Vehicle> result = vehicleServiceMock.findAllAvailable(queryStart, queryEnd);

        // THEN
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllAvailable_RequestedPeriodFullyContainsReservation_VehicleExcluded() {
        // GIVEN - reservation [day 10, day 15], query [day 5, day 20]
        // The requested period fully contains the reservation
        LocalDate now = LocalDate.now();
        Vehicle vehicle = createVehicleWithReservation(now.plusDays(10), now.plusDays(15));

        Mockito.doReturn(List.of(vehicle))
                .when(vehicleRepositoryMock)
                .findAll();

        OffsetDateTime queryStart = toOffsetDateTime(now.plusDays(5));
        OffsetDateTime queryEnd = toOffsetDateTime(now.plusDays(20));

        // WHEN
        List<Vehicle> result = vehicleServiceMock.findAllAvailable(queryStart, queryEnd);

        // THEN
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllAvailable_OverlapOnLeftSide_VehicleExcluded() {
        // GIVEN - reservation [day 10, day 20], query [day 5, day 15]
        // Query overlaps reservation on the left
        LocalDate now = LocalDate.now();
        Vehicle vehicle = createVehicleWithReservation(now.plusDays(10), now.plusDays(20));

        Mockito.doReturn(List.of(vehicle))
                .when(vehicleRepositoryMock)
                .findAll();

        OffsetDateTime queryStart = toOffsetDateTime(now.plusDays(5));
        OffsetDateTime queryEnd = toOffsetDateTime(now.plusDays(15));

        // WHEN
        List<Vehicle> result = vehicleServiceMock.findAllAvailable(queryStart, queryEnd);

        // THEN
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllAvailable_OverlapOnRightSide_VehicleExcluded() {
        // GIVEN - reservation [day 5, day 15], query [day 10, day 20]
        // Query overlaps reservation on the right
        LocalDate now = LocalDate.now();
        Vehicle vehicle = createVehicleWithReservation(now.plusDays(5), now.plusDays(15));

        Mockito.doReturn(List.of(vehicle))
                .when(vehicleRepositoryMock)
                .findAll();

        OffsetDateTime queryStart = toOffsetDateTime(now.plusDays(10));
        OffsetDateTime queryEnd = toOffsetDateTime(now.plusDays(20));

        // WHEN
        List<Vehicle> result = vehicleServiceMock.findAllAvailable(queryStart, queryEnd);

        // THEN
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllAvailable_ExactSameStartDate_VehicleExcluded() {
        // GIVEN - reservation [day 10, day 20], query [day 10, day 15]
        LocalDate now = LocalDate.now();
        Vehicle vehicle = createVehicleWithReservation(now.plusDays(10), now.plusDays(20));

        Mockito.doReturn(List.of(vehicle))
                .when(vehicleRepositoryMock)
                .findAll();

        OffsetDateTime queryStart = toOffsetDateTime(now.plusDays(10));
        OffsetDateTime queryEnd = toOffsetDateTime(now.plusDays(15));

        // WHEN
        List<Vehicle> result = vehicleServiceMock.findAllAvailable(queryStart, queryEnd);

        // THEN
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllAvailable_ExactSameEndDate_VehicleExcluded() {
        // GIVEN - reservation [day 5, day 15], query [day 10, day 15]
        LocalDate now = LocalDate.now();
        Vehicle vehicle = createVehicleWithReservation(now.plusDays(5), now.plusDays(15));

        Mockito.doReturn(List.of(vehicle))
                .when(vehicleRepositoryMock)
                .findAll();

        OffsetDateTime queryStart = toOffsetDateTime(now.plusDays(10));
        OffsetDateTime queryEnd = toOffsetDateTime(now.plusDays(15));

        // WHEN
        List<Vehicle> result = vehicleServiceMock.findAllAvailable(queryStart, queryEnd);

        // THEN
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllAvailable_NoOverlap_VehicleReturned() {
        // GIVEN - reservation [day 5, day 10], query [day 15, day 20]
        // No overlap at all - vehicle should be returned
        LocalDate now = LocalDate.now();
        Vehicle vehicle = createVehicleWithReservation(now.plusDays(5), now.plusDays(10));

        Mockito.doReturn(List.of(vehicle))
                .when(vehicleRepositoryMock)
                .findAll();

        OffsetDateTime queryStart = toOffsetDateTime(now.plusDays(15));
        OffsetDateTime queryEnd = toOffsetDateTime(now.plusDays(20));

        // WHEN
        List<Vehicle> result = vehicleServiceMock.findAllAvailable(queryStart, queryEnd);

        // THEN
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void testFindAllAvailable_VehicleWithNoReservations_VehicleReturned() {
        // GIVEN - vehicle with empty reservation set
        LocalDate now = LocalDate.now();
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setName("FreeVehicle");
        vehicle.setReservationSet(new HashSet<>());

        Mockito.doReturn(List.of(vehicle))
                .when(vehicleRepositoryMock)
                .findAll();

        OffsetDateTime queryStart = toOffsetDateTime(now.plusDays(5));
        OffsetDateTime queryEnd = toOffsetDateTime(now.plusDays(10));

        // WHEN
        List<Vehicle> result = vehicleServiceMock.findAllAvailable(queryStart, queryEnd);

        // THEN
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("FreeVehicle", result.get(0).getName());
    }

    // ========== findAllAvailable - invalid dates ==========

    @Test
    void testFindAllAvailable_StartDateAfterEndDate_ThrowsTripDatesIncorrectException() {
        // GIVEN
        LocalDate now = LocalDate.now();
        OffsetDateTime queryStart = toOffsetDateTime(now.plusDays(20));
        OffsetDateTime queryEnd = toOffsetDateTime(now.plusDays(10));

        // WHEN / THEN
        Assertions.assertThrows(TripDatesIncorrectException.class,
                () -> vehicleServiceMock.findAllAvailable(queryStart, queryEnd));
    }

    @Test
    void testFindAllAvailable_StartDateInThePast_ThrowsTripDatesIncorrectException() {
        // GIVEN
        LocalDate now = LocalDate.now();
        OffsetDateTime queryStart = toOffsetDateTime(now.minusDays(5));
        OffsetDateTime queryEnd = toOffsetDateTime(now.plusDays(10));

        // WHEN / THEN
        Assertions.assertThrows(TripDatesIncorrectException.class,
                () -> vehicleServiceMock.findAllAvailable(queryStart, queryEnd));
    }

    @Test
    void testFindAllAvailable_OnlyStartDateProvided_ThrowsTripDatesIncorrectException() {
        // GIVEN - XOR case: only start date provided
        LocalDate now = LocalDate.now();
        OffsetDateTime queryStart = toOffsetDateTime(now.plusDays(5));

        // WHEN / THEN
        Assertions.assertThrows(TripDatesIncorrectException.class,
                () -> vehicleServiceMock.findAllAvailable(queryStart, null));
    }

    @Test
    void testFindAllAvailable_OnlyEndDateProvided_ThrowsTripDatesIncorrectException() {
        // GIVEN - XOR case: only end date provided
        LocalDate now = LocalDate.now();
        OffsetDateTime queryEnd = toOffsetDateTime(now.plusDays(10));

        // WHEN / THEN
        Assertions.assertThrows(TripDatesIncorrectException.class,
                () -> vehicleServiceMock.findAllAvailable(null, queryEnd));
    }

    // ========== Helper methods ==========

    private Vehicle createVehicleWithReservation(LocalDate resStart, LocalDate resEnd) {
        Reservation reservation = new Reservation();
        reservation.setStartDate(resStart);
        reservation.setEndDate(resEnd);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setName("TestVehicle");
        vehicle.setReservationSet(Set.of(reservation));

        return vehicle;
    }

    private OffsetDateTime toOffsetDateTime(LocalDate date) {
        return date.atStartOfDay().atOffset(ZoneOffset.UTC);
    }
}
