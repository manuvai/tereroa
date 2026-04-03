package fr.manuvai.tereroa.mappers;

import fr.manuvai.tereroa.api.models.UserDto;
import fr.manuvai.tereroa.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto entityToDto(User entity);

}
