package fr.manuvai.tereroa.mappers;

import fr.manuvai.tereroa.api.models.VehicleDto;
import fr.manuvai.tereroa.models.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VehicleMapper {

    VehicleMapper INSTANCE = Mappers.getMapper(VehicleMapper.class);

    VehicleDto entityToDto(Vehicle entity);
}
