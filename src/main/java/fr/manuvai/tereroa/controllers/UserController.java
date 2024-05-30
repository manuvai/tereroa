package fr.manuvai.tereroa.controllers;

import fr.manuvai.tereroa.api.UsersApi;
import fr.manuvai.tereroa.api.models.UserDto;
import fr.manuvai.tereroa.exceptions.NotFoundException;
import fr.manuvai.tereroa.mappers.UserMapper;
import fr.manuvai.tereroa.models.User;
import fr.manuvai.tereroa.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserController implements UsersApi {

    @Autowired
    UserRepository userRepository;

    @Override
    public ResponseEntity<UserDto> getUser(Integer id) {
        User user = userRepository.findById(id.longValue())
                .orElseThrow(NotFoundException::new);

        return ResponseEntity.ok(UserMapper.INSTANCE.entityToDto(user));
    }

    @Override
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = users.stream()
                .map(UserMapper.INSTANCE::entityToDto)
                .toList();

        return ResponseEntity.ok(userDtos);
    }
}
