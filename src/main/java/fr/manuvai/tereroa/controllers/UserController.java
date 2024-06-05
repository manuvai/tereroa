package fr.manuvai.tereroa.controllers;

import fr.manuvai.tereroa.api.UsersApi;
import fr.manuvai.tereroa.api.models.ReservationDto;
import fr.manuvai.tereroa.api.models.UserDto;
import fr.manuvai.tereroa.mappers.ReservationMapper;
import fr.manuvai.tereroa.mappers.UserMapper;
import fr.manuvai.tereroa.models.User;
import fr.manuvai.tereroa.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserController implements UsersApi {

    @Autowired
    UserService userService;

    @Override
    public ResponseEntity<UserDto> getUser(Integer id) {
        User user = userService.findById(id.longValue());

        return ResponseEntity.ok(UserMapper.INSTANCE.entityToDto(user));
    }

    @Override
    public ResponseEntity<List<ReservationDto>> getUserReservations(Integer id) {

        User user = userService.findById(id.longValue());

        List<ReservationDto> userReservationDtos = user.getReservationSet()
                .stream()
                .map(ReservationMapper.INSTANCE::entityToDto)
                .toList();
        return ResponseEntity.ok(userReservationDtos);
    }

    @Override
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDto> userDtos = users.stream()
                .map(UserMapper.INSTANCE::entityToDto)
                .toList();

        return ResponseEntity.ok(userDtos);
    }
}
