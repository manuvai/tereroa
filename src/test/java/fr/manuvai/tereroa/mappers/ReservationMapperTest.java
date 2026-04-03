package fr.manuvai.tereroa.mappers;

import fr.manuvai.tereroa.api.models.ReservationDto;
import fr.manuvai.tereroa.models.Reservation;
import fr.manuvai.tereroa.models.User;
import fr.manuvai.tereroa.models.Vehicle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@SpringBootTest
public class ReservationMapperTest {

    @Autowired
    ReservationMapper reservationMapper;

    // ========== entityToDto tests ==========

    @Test
    void testEntityToDto_AllFieldsMapped() {
        // GIVEN
        User customer = new User();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");

        User owner = new User();
        owner.setId(2L);
        owner.setFirstName("Jane");

        Vehicle vehicle = new Vehicle();
        vehicle.setId(10L);
        vehicle.setName("Toyota");
        vehicle.setPricePerDay(new BigDecimal("15.50"));
        vehicle.setOwner(owner);

        Reservation reservation = new Reservation();
        reservation.setId(100L);
        reservation.setStartDate(LocalDate.of(2026, 6, 1));
        reservation.setEndDate(LocalDate.of(2026, 6, 6));
        reservation.setCustomer(customer);
        reservation.setVehicle(vehicle);

        // WHEN
        ReservationDto dto = reservationMapper.entityToDto(reservation);

        // THEN
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(100, dto.getId());

        // Dates mapped to OffsetDateTime at UTC midnight
        OffsetDateTime expectedStart = LocalDate.of(2026, 6, 1).atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime expectedEnd = LocalDate.of(2026, 6, 6).atStartOfDay().atOffset(ZoneOffset.UTC);
        Assertions.assertEquals(expectedStart, dto.getStartDate());
        Assertions.assertEquals(expectedEnd, dto.getEndDate());

        // Customer mapped
        Assertions.assertNotNull(dto.getCustomer());
        Assertions.assertEquals(1, dto.getCustomer().getId());
        Assertions.assertEquals("John", dto.getCustomer().getFirstName());
        Assertions.assertEquals("Doe", dto.getCustomer().getLastName());
        Assertions.assertEquals("john@example.com", dto.getCustomer().getEmail());

        // Vehicle mapped
        Assertions.assertNotNull(dto.getVehicle());
        Assertions.assertEquals(10, dto.getVehicle().getId());
        Assertions.assertEquals("Toyota", dto.getVehicle().getName());
        Assertions.assertEquals(new BigDecimal("15.50"), dto.getVehicle().getPricePerDay());

        // Total: 5 days * 15.50 = 77.50
        Assertions.assertEquals(0, new BigDecimal("77.50").compareTo(dto.getTotal()));
    }

    // ========== getTotalAmount tests ==========

    @Test
    void testGetTotalAmount_FiveDaysAtFifteenFifty() {
        // GIVEN
        Vehicle vehicle = new Vehicle();
        vehicle.setPricePerDay(new BigDecimal("15.50"));

        Reservation reservation = new Reservation();
        reservation.setStartDate(LocalDate.of(2026, 6, 1));
        reservation.setEndDate(LocalDate.of(2026, 6, 6));
        reservation.setVehicle(vehicle);

        // WHEN
        BigDecimal total = reservationMapper.getTotalAmount(reservation);

        // THEN - 5 days * 15.50 = 77.50
        Assertions.assertEquals(0, new BigDecimal("77.50").compareTo(total));
    }

    @Test
    void testGetTotalAmount_NullReservation_ReturnsZero() {
        // WHEN
        BigDecimal total = reservationMapper.getTotalAmount(null);

        // THEN
        Assertions.assertEquals(0, BigDecimal.ZERO.compareTo(total));
    }

    @Test
    void testGetTotalAmount_NullDates_ReturnsZero() {
        // GIVEN
        Reservation reservation = new Reservation();
        reservation.setStartDate(null);
        reservation.setEndDate(null);

        // WHEN
        BigDecimal total = reservationMapper.getTotalAmount(reservation);

        // THEN
        Assertions.assertEquals(0, BigDecimal.ZERO.compareTo(total));
    }

    @Test
    void testGetTotalAmount_NullStartDate_ReturnsZero() {
        // GIVEN
        Reservation reservation = new Reservation();
        reservation.setStartDate(null);
        reservation.setEndDate(LocalDate.of(2026, 6, 6));

        // WHEN
        BigDecimal total = reservationMapper.getTotalAmount(reservation);

        // THEN
        Assertions.assertEquals(0, BigDecimal.ZERO.compareTo(total));
    }

    @Test
    void testGetTotalAmount_NullVehicle_ReturnsZero() {
        // GIVEN
        Reservation reservation = new Reservation();
        reservation.setStartDate(LocalDate.of(2026, 6, 1));
        reservation.setEndDate(LocalDate.of(2026, 6, 6));
        reservation.setVehicle(null);

        // WHEN
        BigDecimal total = reservationMapper.getTotalAmount(reservation);

        // THEN - 5 days * 0 (no vehicle) = 0
        Assertions.assertEquals(0, BigDecimal.ZERO.compareTo(total));
    }

    @Test
    void testGetTotalAmount_NullPricePerDay_ReturnsZero() {
        // GIVEN
        Vehicle vehicle = new Vehicle();
        vehicle.setPricePerDay(null);

        Reservation reservation = new Reservation();
        reservation.setStartDate(LocalDate.of(2026, 6, 1));
        reservation.setEndDate(LocalDate.of(2026, 6, 6));
        reservation.setVehicle(vehicle);

        // WHEN
        BigDecimal total = reservationMapper.getTotalAmount(reservation);

        // THEN
        Assertions.assertEquals(0, BigDecimal.ZERO.compareTo(total));
    }

    // ========== localDateToOffsetDateTime tests ==========

    @Test
    void testLocalDateToOffsetDateTime_NullInput_ReturnsNull() {
        // WHEN
        OffsetDateTime result = reservationMapper.localDateToOffsetDateTime(null);

        // THEN
        Assertions.assertNull(result);
    }

    @Test
    void testLocalDateToOffsetDateTime_ValidInput_ReturnsCorrectOffsetDateTime() {
        // GIVEN
        LocalDate date = LocalDate.of(2026, 7, 15);

        // WHEN
        OffsetDateTime result = reservationMapper.localDateToOffsetDateTime(date);

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2026, result.getYear());
        Assertions.assertEquals(7, result.getMonthValue());
        Assertions.assertEquals(15, result.getDayOfMonth());
        Assertions.assertEquals(0, result.getHour());
        Assertions.assertEquals(0, result.getMinute());
        Assertions.assertEquals(0, result.getSecond());
        Assertions.assertEquals(ZoneOffset.UTC, result.getOffset());
    }
}
