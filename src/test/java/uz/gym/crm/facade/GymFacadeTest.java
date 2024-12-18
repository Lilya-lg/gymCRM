package uz.gym.crm.facade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;
import uz.gym.crm.service.TraineeServiceImpl;
import uz.gym.crm.service.TrainerServiceImpl;
import uz.gym.crm.service.TrainingServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GymFacadeTest {

    @InjectMocks
    private GymFacade gymFacade;

    @Mock
    private TraineeServiceImpl traineeService;

    @Mock
    private TrainerServiceImpl trainerService;

    @Mock
    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
    void testSetAndGetTrainingService() {
        TrainingServiceImpl newTrainingService = mock(TrainingServiceImpl.class);
        gymFacade.setTrainingService(newTrainingService);

        assertEquals(newTrainingService, gymFacade.getTrainingService());
    }

    @Test
    void testUseTraineeService() {
        List<Trainee> trainees = Arrays.asList(new Trainee(), new Trainee());
        when(traineeService.getAll()).thenReturn(trainees);

        List<Trainee> result = gymFacade.getTraineeService().getAll();

        assertEquals(2, result.size());
        verify(traineeService).getAll();
    }

    @Test
    void testUseTrainerService() {
        List<Trainer> trainers = Arrays.asList(new Trainer(), new Trainer());
        when(trainerService.getAll()).thenReturn(trainers);

        List<Trainer> result = gymFacade.getTrainerService().getAll();

        assertEquals(2, result.size());
        verify(trainerService).getAll();
    }

    @Test
    void testUseTrainingService() {
        List<Training> trainings = Arrays.asList(new Training(), new Training());
        gymFacade.setTrainingService(trainingService);
        when(trainingService.getAll()).thenReturn(trainings);

        List<Training> result = gymFacade.getTrainingService().getAll();

        assertEquals(2, result.size());
        verify(trainingService).getAll();
    }

}
