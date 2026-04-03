package fr.manuvai.tereroa.services;

import fr.manuvai.tereroa.exceptions.NotFoundException;
import fr.manuvai.tereroa.models.Reservation;
import fr.manuvai.tereroa.models.User;
import fr.manuvai.tereroa.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Set<Reservation> findReservationsByUserId(Long id) {
        return findById(id).getReservationSet();
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
