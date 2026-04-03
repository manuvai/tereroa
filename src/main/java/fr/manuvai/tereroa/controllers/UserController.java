package fr.manuvai.tereroa.controllers;

import fr.manuvai.tereroa.api.UsersApi;
import fr.manuvai.tereroa.api.models.ReservationDto;
import fr.manuvai.tereroa.api.models.UserDto;
import fr.manuvai.tereroa.mappers.ReservationMapper;
import fr.manuvai.tereroa.mappers.UserMapper;
import fr.manuvai.tereroa.models.User;
import fr.manuvai.tereroa.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ReservationMapper reservationMapper;

    @Override
    public ResponseEntity<UserDto> getUser(Integer id) {
        User user = userService.findById(id.longValue());

        return ResponseEntity.ok(userMapper.entityToDto(user));
    }

    @Override
    public ResponseEntity<List<ReservationDto>> getUserReservations(Integer id) {
        List<ReservationDto> userReservationDtos = userService.findReservationsByUserId(id.longValue())
                .stream()
                .map(reservationMapper::entityToDto)
                .toList();
        return ResponseEntity.ok(userReservationDtos);
    }

    @Override
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDto> userDtos = users.stream()
                .map(userMapper::entityToDto)
                .toList();

        return ResponseEntity.ok(userDtos);
    }
}
