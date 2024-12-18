package uz.gym.crm.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.domain.Training;
import uz.gym.crm.domain.TrainingType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceImplTest {
    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Mock
    private TrainingDAOImpl trainingDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTraining() {
        Training training = createTestTraining(1L);

        trainingService.create(training);

        verify(trainingDAO).create(training);
    }

    @Test
    void testGetTrainingById() {
        Training training = createTestTraining(1L);
        when(trainingDAO.read(1L)).thenReturn(Optional.of(training));

        Training result = trainingService.read(1L);

        assertNotNull(result);
        assertEquals(training, result);
        verify(trainingDAO).read(1L);
    }

    @Test
    void testGetTrainingById_NotFound() {
        when(trainingDAO.read(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            trainingService.read(1L);
        });

        assertEquals("Entity not found for ID: 1", exception.getMessage());
        verify(trainingDAO).read(1L);
    }

    @Test
    void testUpdateTraining() {
        Training training = createTestTraining(1L);

        trainingService.update(training);

        verify(trainingDAO).update(training);
    }

    @Test
    void testDeleteTraining() {
        trainingService.delete(1L);

        verify(trainingDAO).delete(1L);
    }

    @Test
    void testGetAllTrainings() {
        Training training1 = createTestTraining(1L);
        Training training2 = createTestTraining(2L);
        List<Training> trainings = Arrays.asList(training1, training2);

        when(trainingDAO.getAll()).thenReturn(trainings);

        List<Training> result = trainingService.getAll();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(trainings));
        verify(trainingDAO).getAll();
    }

    private Training createTestTraining(Long id) {
        Training training = new Training();
        training.setId(id);
        training.setTrainingName("Java Basics");
        training.setTrainingType(TrainingType.YOGA);
        training.setTrainingDate(LocalDate.of(2024, 12, 18));
        training.setTrainingDuration(5);
        return training;
    }
}
