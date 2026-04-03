package fr.manuvai.tereroa.services;

import fr.manuvai.tereroa.exceptions.NotFoundException;
import fr.manuvai.tereroa.models.User;
import fr.manuvai.tereroa.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserService userServiceMock;

    @Mock
    UserRepository userRepositoryMock;

    // ========== findById tests ==========

    @Test
    void testFindById_Found() {
        // GIVEN
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        Mockito.doReturn(Optional.of(user))
                .when(userRepositoryMock)
                .findById(userId);

        // WHEN
        User result = userServiceMock.findById(userId);

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertEquals(userId, result.getId());
        Assertions.assertEquals("John", result.getFirstName());
        Assertions.assertEquals("Doe", result.getLastName());
        Assertions.assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void testFindById_NotFound_ThrowsNotFoundException() {
        // GIVEN
        Long userId = 999L;

        Mockito.doReturn(Optional.empty())
                .when(userRepositoryMock)
                .findById(userId);

        // WHEN / THEN
        Assertions.assertThrows(NotFoundException.class,
                () -> userServiceMock.findById(userId));
    }

    // ========== findAll tests ==========

    @Test
    void testFindAll_ReturnsList() {
        // GIVEN
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Alice");

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Bob");

        Mockito.doReturn(List.of(user1, user2))
                .when(userRepositoryMock)
                .findAll();

        // WHEN
        List<User> result = userServiceMock.findAll();

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Alice", result.get(0).getFirstName());
        Assertions.assertEquals("Bob", result.get(1).getFirstName());
    }

    @Test
    void testFindAll_ReturnsEmptyList() {
        // GIVEN
        Mockito.doReturn(Collections.emptyList())
                .when(userRepositoryMock)
                .findAll();

        // WHEN
        List<User> result = userServiceMock.findAll();

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }
}
