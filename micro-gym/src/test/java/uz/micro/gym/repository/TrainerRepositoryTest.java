package uz.micro.gym.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import uz.micro.gym.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class TrainerRepositoryTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername() {

        User user = new User();
        user.setFirstName("trainee");
        user.setLastName("1");
        user.setUsername("trainer1");
        user.setPassword("password123");
        userRepository.save(user);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingType(PredefinedTrainingType.PILATES);
        trainingTypeRepository.save(trainingType);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);
        trainerRepository.save(trainer);

        Optional<Trainer> foundTrainer = trainerRepository.findByUsername("trainer1");
        assertTrue(foundTrainer.isPresent());
        assertEquals("trainer1", foundTrainer.get().getUser().getUsername());
    }

    @Test
    void testGetUnassignedTrainersByTraineeUsername() {
        User traineeUser = new User();
        traineeUser.setFirstName("trainee");
        traineeUser.setLastName("1");
        traineeUser.setUsername("trainee1");
        traineeUser.setPassword("password123");
        userRepository.save(traineeUser);


        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);


        User trainerUser = new User();
        trainerUser.setFirstName("trainer");
        trainerUser.setLastName("1");
        trainerUser.setUsername("trainer1");
        trainerUser.setPassword("password123");
        userRepository.save(trainerUser);
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingType(PredefinedTrainingType.PILATES);
        trainingTypeRepository.save(trainingType);

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(trainingType);
        trainerRepository.save(trainer);


        List<Trainer> unassignedTrainers = trainerRepository.getUnassignedTrainersByTraineeUsername("trainee1");
        assertFalse(unassignedTrainers.isEmpty());
        assertEquals("trainer1", unassignedTrainers.get(0).getUser().getUsername());
    }
}