package uz.gym.crm.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import uz.gym.crm.domain.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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