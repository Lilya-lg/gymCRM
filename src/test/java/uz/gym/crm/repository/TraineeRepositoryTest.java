package uz.gym.crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class TraineeRepositoryTest {

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername() {

        User user = new User();
        user.setFirstName("trainee");
        user.setLastName("1");
        user.setUsername("trainee1");
        user.setPassword("password123");
        userRepository.save(user);


        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.now());
        traineeRepository.save(trainee);

        Optional<Trainee> foundTrainee = traineeRepository.findByUsername("trainee1");
        assertTrue(foundTrainee.isPresent());
        assertEquals("trainee1", foundTrainee.get().getUser().getUsername());
    }

    @Test
    void testDeleteByUsername() {

        User user = new User();
        user.setFirstName("trainee");
        user.setLastName("1");
        user.setUsername("trainee1");
        user.setPassword("password123");
        userRepository.save(user);


        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.now());
        traineeRepository.save(trainee);


        traineeRepository.deleteByUsername("trainee1");
        Optional<Trainee> deletedTrainee = traineeRepository.findByUsername("trainee1");
        assertFalse(deletedTrainee.isPresent());
    }
}
