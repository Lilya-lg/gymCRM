package uz.gym.crm.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import uz.gym.crm.domain.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class TrainingRepositoryTest {

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private TrainingTypeRepository trainingTypeRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByCriteria() {

        User traineeUser = new User();
        traineeUser.setFirstName("trainee");
        traineeUser.setLastName("1");
        traineeUser.setUsername("trainee1");
        traineeUser.setPassword("password123");
        userRepository.save(traineeUser);


        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);
        trainee.setDateOfBirth(LocalDate.now());
        traineeRepository.save(trainee);

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


        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingDate(LocalDate.of(2023, 10, 1));
        training.setTrainingDuration(60);
        training.setTrainingName("Pilates");
        training.setTrainingType(trainingType);
        trainingRepository.save(training);


        List<Training> trainings = trainingRepository.findByCriteria("trainee1", PredefinedTrainingType.PILATES, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), "trainer1");
        assertFalse(trainings.isEmpty());
        assertEquals("trainee1", trainings.get(0).getTrainee().getUser().getUsername());
    }

    @Test
    void testFindByCriteriaForTrainer() {

        User traineeUser = new User();
        traineeUser.setFirstName("trainee");
        traineeUser.setLastName("1");
        traineeUser.setUsername("trainee1");
        traineeUser.setPassword("password123");
        userRepository.save(traineeUser);


        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);
        trainee.setDateOfBirth(LocalDate.now());
        traineeRepository.save(trainee);
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingType(PredefinedTrainingType.PILATES);
        trainingTypeRepository.save(trainingType);

        User trainerUser = new User();
        trainerUser.setFirstName("trainer");
        trainerUser.setLastName("1");
        trainerUser.setUsername("trainer1");
        trainerUser.setPassword("password123");
        userRepository.save(trainerUser);


        Trainer trainer = new Trainer();
        trainerUser.setFirstName("trainer");
        trainerUser.setLastName("1");
        trainer.setUser(trainerUser);
        trainer.setSpecialization(trainingType);
        trainerRepository.save(trainer);


        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingDate(LocalDate.of(2023, 10, 1));
        training.setTrainingDuration(60);
        training.setTrainingName("Pilates");
        training.setTrainingType(trainingType);

        trainingRepository.save(training);


        List<Training> trainings = trainingRepository.findByCriteriaForTrainer("trainer1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), "trainee1");
        assertFalse(trainings.isEmpty());
        assertEquals("trainer1", trainings.get(0).getTrainer().getUser().getUsername());
    }
}