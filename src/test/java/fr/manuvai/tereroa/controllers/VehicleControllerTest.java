package fr.manuvai.tereroa.controllers;

import fr.manuvai.tereroa.api.models.ReservationDto;
import fr.manuvai.tereroa.api.models.UserDto;
import fr.manuvai.tereroa.api.models.VehicleDto;
import fr.manuvai.tereroa.config.SecurityConfig;
import fr.manuvai.tereroa.exceptions.NotFoundException;
import fr.manuvai.tereroa.mappers.ReservationMapper;
import fr.manuvai.tereroa.mappers.VehicleMapper;
import fr.manuvai.tereroa.models.Reservation;
import fr.manuvai.tereroa.models.Vehicle;
import fr.manuvai.tereroa.services.VehicleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
@Import(SecurityConfig.class)
public class VehicleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    VehicleService vehicleService;

    @MockBean
    VehicleMapper vehicleMapper;

    @MockBean
    ReservationMapper reservationMapper;

    // ========== GET /vehicles ==========

    @Test
    void testGetAllVehicles_Returns200WithList() throws Exception {
        // GIVEN
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setId(1L);
        vehicle1.setName("Toyota");

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setId(2L);
        vehicle2.setName("Honda");

        VehicleDto dto1 = VehicleDto.builder()
                .id(1)
                .name("Toyota")
                .pricePerDay(new BigDecimal("20.00"))
                .build();
        VehicleDto dto2 = VehicleDto.builder()
                .id(2)
                .name("Honda")
                .pricePerDay(new BigDecimal("25.00"))
                .build();

        Mockito.when(vehicleService.findAllAvailable(null, null))
                .thenReturn(List.of(vehicle1, vehicle2));
        Mockito.when(vehicleMapper.entityToDto(vehicle1)).thenReturn(dto1);
        Mockito.when(vehicleMapper.entityToDto(vehicle2)).thenReturn(dto2);

        // WHEN / THEN
        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Toyota")))
                .andExpect(jsonPath("$[1].name", is("Honda")));
    }

    // ========== GET /vehicles/{id} ==========

    @Test
    void testGetVehicle_Returns200() throws Exception {
        // GIVEN
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setName("Toyota");
        vehicle.setReservationSet(new HashSet<>());

        VehicleDto dto = VehicleDto.builder()
                .id(1)
                .name("Toyota")
                .pricePerDay(new BigDecimal("20.00"))
                .owner(UserDto.builder().id(1).firstName("John").build())
                .build();

        Mockito.when(vehicleService.findById(1L)).thenReturn(vehicle);
        Mockito.when(vehicleMapper.entityToDto(vehicle)).thenReturn(dto);

        // WHEN / THEN
        mockMvc.perform(get("/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Toyota")))
                .andExpect(jsonPath("$.pricePerDay", is(20.00)))
                .andExpect(jsonPath("$.owner.firstName", is("John")));
    }

    @Test
    void testGetVehicle_NotFound_Returns404() throws Exception {
        // GIVEN
        Mockito.when(vehicleService.findById(999L))
                .thenThrow(new NotFoundException());

        // WHEN / THEN
        mockMvc.perform(get("/vehicles/999"))
                .andExpect(status().isNotFound());
    }

    // ========== GET /vehicles/{id}/reservations ==========

    @Test
    void testGetVehicleReservations_Returns200() throws Exception {
        // GIVEN
        Reservation reservation = new Reservation();
        reservation.setId(10L);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setName("Toyota");
        vehicle.setReservationSet(Set.of(reservation));

        ReservationDto resDto = ReservationDto.builder()
                .id(10)
                .total(new BigDecimal("77.50"))
                .startDate(OffsetDateTime.of(2026, 6, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .endDate(OffsetDateTime.of(2026, 6, 6, 0, 0, 0, 0, ZoneOffset.UTC))
                .build();

        Mockito.when(vehicleService.findReservationsByVehicleId(1L)).thenReturn(Set.of(reservation));
        Mockito.when(reservationMapper.entityToDto(reservation)).thenReturn(resDto);

        // WHEN / THEN
        mockMvc.perform(get("/vehicles/1/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(10)))
                .andExpect(jsonPath("$[0].total", is(77.50)));
    }
}
