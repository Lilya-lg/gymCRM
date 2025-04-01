package uz.micro.gym.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import uz.micro.gym.domain.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername() {

        User user = new User();
        user.setFirstName("user");
        user.setLastName("1");
        user.setUsername("user1");
        user.setPassword("password123");
        userRepository.save(user);


        Optional<User> foundUser = userRepository.findByUsername("user1");
        assertTrue(foundUser.isPresent());
        assertEquals("user1", foundUser.get().getUsername());
    }

    @Test
    void testFindByUsernameAndPassword() {

        User user = new User();
        user.setFirstName("user");
        user.setLastName("1");
        user.setUsername("user1");
        user.setPassword("password123");
        userRepository.save(user);


        Optional<User> foundUser = userRepository.findByUsernameAndPassword("user1", "password123");
        assertTrue(foundUser.isPresent());
        assertEquals("user1", foundUser.get().getUsername());
    }
}