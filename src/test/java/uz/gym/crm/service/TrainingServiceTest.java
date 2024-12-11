package uz.gym.crm.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uz.gym.crm.dao.TrainingDAO;
import uz.gym.crm.domain.Training;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    private TrainingDAO trainingDAO;
    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        trainingDAO = Mockito.mock(TrainingDAO.class);
        trainingService = new TrainingServiceImpl(trainingDAO);
    }

    @Test
    void testCreateTraining() {
        Training training = new Training();
        training.setId(1);
        training.setTrainingName("Yoga Basics");
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);
        trainingService.create(training);
        verify(trainingDAO, times(1)).create(training);
    }

    @Test
    void testGetTrainingById_ReturnsCorrectTraining() {

        Training training = new Training();
        training.setId(1);
        when(trainingDAO.read(1)).thenReturn(training);


        Training result = trainingService.read(1);


        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(trainingDAO, times(1)).read(1);
    }

    @Test
    void testGetAllTrainings_ReturnsListOfTrainings() {
        List<Training> trainings = List.of(new Training(), new Training());
        when(trainingDAO.getAll()).thenReturn(trainings);

        List<Training> result = trainingService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(trainingDAO, times(1)).getAll();
    }
}
