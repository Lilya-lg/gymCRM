package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.gym.crm.domain.PredefinedTrainingType;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;
import uz.gym.crm.repository.TraineeRepository;
import uz.gym.crm.repository.TrainerRepository;
import uz.gym.crm.repository.TrainingRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTraining() {
        Training training = new Training();
        String username = "testUser";
        String password = "password";

        trainingService.addTraining(training, username, password);

        verify(trainingRepository, times(1)).save(training);
    }

    @Test
    void findByCriteriaForTrainer() {
        String trainerUsername = "testUser";
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now();
        String traineeName = "traineeUser";

        when(trainingRepository.findByCriteriaForTrainer(trainerUsername, fromDate, toDate, traineeName)).thenReturn(Collections.emptyList());

        List<Training> result = trainingService.findByCriteriaForTrainer(trainerUsername, fromDate, toDate, traineeName);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByCriteria() {
        String traineeUsername = "testUser";
        String trainingType = "CARDIO";
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now();
        String trainerName = "trainerUser";

        when(trainingRepository.findByCriteria(traineeUsername, PredefinedTrainingType.CARDIO, fromDate, toDate, trainerName)).thenReturn(Collections.emptyList());

        List<Training> result = trainingService.findByCriteria(traineeUsername, trainingType, fromDate, toDate, trainerName);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void linkTraineeTrainer() {
        Training training = new Training();
        String traineeName = "traineeUser";
        String trainerName = "trainerUser";

        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();

        when(traineeRepository.findByUsername(traineeName)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername(trainerName)).thenReturn(Optional.of(trainer));

        trainingService.linkTraineeTrainer(training, traineeName, trainerName);

        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());
    }
}