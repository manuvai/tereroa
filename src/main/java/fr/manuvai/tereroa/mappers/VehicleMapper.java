package fr.manuvai.tereroa.mappers;

import fr.manuvai.tereroa.api.models.VehicleDto;
import fr.manuvai.tereroa.models.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UserMapper.class})
public interface VehicleMapper {

    VehicleMapper INSTANCE = Mappers.getMapper(VehicleMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "pricePerDay", target = "pricePerDay")
    @Mapping(source = "owner", target = "owner")
    VehicleDto entityToDto(Vehicle entity);
}
