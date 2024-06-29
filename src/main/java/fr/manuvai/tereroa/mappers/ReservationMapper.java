package fr.manuvai.tereroa.mappers;

import fr.manuvai.tereroa.api.models.ReservationDto;
import fr.manuvai.tereroa.models.Reservation;
import fr.manuvai.tereroa.models.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Mapper(uses = {VehicleMapper.class, UserMapper.class})
public interface ReservationMapper {

    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Named("dateToOffsetDateTime")
    default OffsetDateTime dateToOffsetDateTime(Date date) {
        return date == null
                ? null
                : date.toInstant().atOffset(ZoneOffset.UTC);
    }

    @Named("getTotalAmount")
    default BigDecimal getTotalAmount(Reservation reservation) {
        if (reservation == null) {
            return BigDecimal.ZERO;
        }

        Date startDate = reservation.getStartDate();
        Date endDate = reservation.getEndDate();

        if (startDate == null || endDate == null) {
            return BigDecimal.ZERO;
        }

        long diffInMillis = Math.abs(endDate.getTime() - startDate.getTime());

        long diff = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

        Double costPerDay = Optional.ofNullable(reservation.getVehicle())
                .map(Vehicle::getPricePerDay)
                .orElse(0D);

        return BigDecimal.valueOf(costPerDay * diff);
    }

    @Mapping(source = "id", target = "id")
    @Mapping(source = "customer", target = "customer")
    @Mapping(source = "vehicle", target = "vehicle")
    @Mapping(source = "startDate", target = "startDate", qualifiedByName = "dateToOffsetDateTime")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "dateToOffsetDateTime")
    @Mapping(source = "entity", target = "total", qualifiedByName = "getTotalAmount")
    ReservationDto entityToDto(Reservation entity);
}
