package fr.manuvai.tereroa.mappers;

import fr.manuvai.tereroa.api.models.UserDto;
import fr.manuvai.tereroa.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto entityToDto(User entity);

}
