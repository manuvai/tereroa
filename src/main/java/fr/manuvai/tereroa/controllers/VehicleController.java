package fr.manuvai.tereroa.controllers;

import fr.manuvai.tereroa.api.VehiclesApi;
import fr.manuvai.tereroa.api.models.ReservationDto;
import fr.manuvai.tereroa.api.models.VehicleDto;
import fr.manuvai.tereroa.mappers.ReservationMapper;
import fr.manuvai.tereroa.mappers.VehicleMapper;
import fr.manuvai.tereroa.models.Vehicle;
import fr.manuvai.tereroa.services.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class VehicleController implements VehiclesApi {

    private final VehicleService vehicleService;
    private final VehicleMapper vehicleMapper;
    private final ReservationMapper reservationMapper;

    @Override
    public ResponseEntity<VehicleDto> getVehicle(Integer id) {
        Vehicle vehicle = vehicleService.findById(id.longValue());

        VehicleDto vehicleDto = vehicleMapper.entityToDto(vehicle);

        return ResponseEntity.ok(vehicleDto);
    }

    @Override
    public ResponseEntity<List<ReservationDto>> getVehicleReservations(Integer id) {
        List<ReservationDto> reservationDtoList = vehicleService.findReservationsByVehicleId(id.longValue())
                .stream()
                .map(reservationMapper::entityToDto)
                .toList();

        return ResponseEntity.ok(reservationDtoList);
    }

    @Override
    public ResponseEntity<List<VehicleDto>> getAllVehicles(OffsetDateTime tripStartDate, OffsetDateTime tripEndDate) {
        List<Vehicle> vehiclesAvailable = vehicleService.findAllAvailable(tripStartDate, tripEndDate);

        List<VehicleDto> vehicleDtos = vehiclesAvailable.stream()
                .map(vehicleMapper::entityToDto)
                .toList();

        return ResponseEntity.ok(vehicleDtos);
    }
}
