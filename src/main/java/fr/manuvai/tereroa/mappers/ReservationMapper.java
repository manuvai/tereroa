package fr.manuvai.tereroa.mappers;

import fr.manuvai.tereroa.api.models.ReservationDto;
import fr.manuvai.tereroa.models.Reservation;
import fr.manuvai.tereroa.models.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Mapper(componentModel = "spring", uses = {VehicleMapper.class, UserMapper.class})
public interface ReservationMapper {

    @Named("localDateToOffsetDateTime")
    default OffsetDateTime localDateToOffsetDateTime(LocalDate date) {
        return date == null
                ? null
                : date.atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    @Named("getTotalAmount")
    default BigDecimal getTotalAmount(Reservation reservation) {
        if (reservation == null) {
            return BigDecimal.ZERO;
        }

        LocalDate startDate = reservation.getStartDate();
        LocalDate endDate = reservation.getEndDate();

        if (startDate == null || endDate == null) {
            return BigDecimal.ZERO;
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);

        BigDecimal costPerDay = Optional.ofNullable(reservation.getVehicle())
                .map(Vehicle::getPricePerDay)
                .orElse(BigDecimal.ZERO);

        return costPerDay.multiply(BigDecimal.valueOf(days));
    }

    @Mapping(source = "startDate", target = "startDate", qualifiedByName = "localDateToOffsetDateTime")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "localDateToOffsetDateTime")
    @Mapping(source = "entity", target = "total", qualifiedByName = "getTotalAmount")
    ReservationDto entityToDto(Reservation entity);
}
