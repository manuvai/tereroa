package fr.manuvai.tereroa.controllers;

import fr.manuvai.tereroa.api.VehiclesApi;
import fr.manuvai.tereroa.api.models.VehicleDto;
import fr.manuvai.tereroa.exceptions.NotFoundException;
import fr.manuvai.tereroa.exceptions.TripDatesIncorrectException;
import fr.manuvai.tereroa.mappers.VehicleMapper;
import fr.manuvai.tereroa.models.Vehicle;
import fr.manuvai.tereroa.repositories.VehicleRepository;
import fr.manuvai.tereroa.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Controller
public class VehicleController implements VehiclesApi {

    @Autowired
    VehicleService vehicleService;

    @Autowired
    VehicleRepository vehicleRepository;

    @Override
    public ResponseEntity<VehicleDto> getVehicle(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id.longValue())
                .orElseThrow(NotFoundException::new);

        VehicleDto vehicleDto = VehicleMapper.INSTANCE.entityToDto(vehicle);

        return ResponseEntity.ok(vehicleDto);
    }

    @Override
    public ResponseEntity<List<VehicleDto>> getAllVehicles(OffsetDateTime tripStartDate, OffsetDateTime tripEndDate) {
        if (tripStartDate == null ^ tripEndDate == null) {
            throw new TripDatesIncorrectException();
        }

        if (tripStartDate != null) {
            OffsetDateTime now = OffsetDateTime.now();
            boolean isInvalid = tripEndDate.isBefore(tripStartDate) || tripStartDate.isBefore(now);
            if (isInvalid) {
                throw new TripDatesIncorrectException();
            }
        }

        List<Vehicle> vehiclesAvailable = vehicleService.findAllAvailable(tripStartDate, tripEndDate);

        List<VehicleDto> vehicleDtos = vehiclesAvailable.stream()
                .map(VehicleMapper.INSTANCE::entityToDto)
                .toList();

        return ResponseEntity.ok(vehicleDtos);
    }
}
