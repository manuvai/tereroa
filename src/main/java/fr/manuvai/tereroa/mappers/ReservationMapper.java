package fr.manuvai.tereroa.mappers;

import fr.manuvai.tereroa.api.models.ReservationDto;
import fr.manuvai.tereroa.api.models.VehicleDto;
import fr.manuvai.tereroa.models.Reservation;
import org.aspectj.lang.annotation.Before;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Mapper(uses = {VehicleMapper.class, UserMapper.class})
public interface ReservationMapper {

    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Named("dateToOffsetDateTime")
    default OffsetDateTime dateToOffsetDateTime(Date date) {
        return date == null
                ? null
                : date.toInstant().atOffset(ZoneOffset.UTC);
    }

    @Mapping(source = "id", target = "id")
    @Mapping(source = "customer", target = "customer")
    @Mapping(source = "vehicle", target = "vehicle")
    @Mapping(source = "startDate", target = "startDate", qualifiedByName = "dateToOffsetDateTime")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "dateToOffsetDateTime")
    ReservationDto entityToDto(Reservation entity);
}
