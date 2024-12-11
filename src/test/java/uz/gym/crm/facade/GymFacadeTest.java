package uz.gym.crm.facade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;
import uz.gym.crm.service.TraineeService;
import uz.gym.crm.service.TrainerService;
import uz.gym.crm.service.TrainingService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GymFacadeTest {

    private GymFacade gymFacade;
    private TraineeService traineeService;
    private TrainerService trainerService;
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        traineeService = Mockito.mock(TraineeService.class);
        trainerService = Mockito.mock(TrainerService.class);
        trainingService = Mockito.mock(TrainingService.class);

        gymFacade = new GymFacade(traineeService, trainerService);
        gymFacade.setTrainingService(trainingService);
    }

    @Test
    void testGetTraineeService() {
        assertNotNull(gymFacade.getTraineeService());
        assertEquals(traineeService, gymFacade.getTraineeService());
    }

    @Test
    void testGetTrainerService() {
        assertNotNull(gymFacade.getTrainerService());
        assertEquals(trainerService, gymFacade.getTrainerService());
    }

    @Test
    void testGetTrainingService() {
        assertNotNull(gymFacade.getTrainingService());
        assertEquals(trainingService, gymFacade.getTrainingService());
    }

    @Test
    void testCreateTrainee() {
        Trainee trainee = new Trainee();
        gymFacade.getTraineeService().create(trainee);
        verify(traineeService, times(1)).create(trainee);
    }

    @Test
    void testGetAllTrainees() {
        List<Trainee> trainees = List.of(new Trainee(), new Trainee());
        when(traineeService.getAll()).thenReturn(trainees);
        List<Trainee> result = gymFacade.getTraineeService().getAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(traineeService, times(1)).getAll();
    }

    @Test
    void testCreateTrainer() {
        Trainer trainer = new Trainer();
        gymFacade.getTrainerService().create(trainer);
        verify(trainerService, times(1)).create(trainer);
    }

    @Test
    void testGetAllTrainers() {
        List<Trainer> trainers = List.of(new Trainer(), new Trainer());
        when(trainerService.getAll()).thenReturn(trainers);
        List<Trainer> result = gymFacade.getTrainerService().getAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(trainerService, times(1)).getAll();
    }

    @Test
    void testCreateTraining() {
        Training training = new Training();
        gymFacade.getTrainingService().create(training);
        verify(trainingService, times(1)).create(training);
    }

    @Test
    void testGetAllTrainings() {
        List<Training> trainings = List.of(new Training(), new Training());
        when(trainingService.getAll()).thenReturn(trainings);
        List<Training> result = gymFacade.getTrainingService().getAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(trainingService, times(1)).getAll();
    }
}
