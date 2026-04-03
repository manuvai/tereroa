package fr.manuvai.tereroa.mappers;

import fr.manuvai.tereroa.api.models.VehicleDto;
import fr.manuvai.tereroa.models.Vehicle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface VehicleMapper {

    VehicleDto entityToDto(Vehicle entity);
}
