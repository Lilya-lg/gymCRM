package uz.gym.crm.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;
import uz.gym.crm.domain.TrainingType;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingDAOImplTest {
    private TrainingDAOImpl trainingDAO;

    @Mock
    private Map<Long, Training> mockStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainingDAO = new TrainingDAOImpl(mockStorage);
    }

    @Test
    void testCreate() {
        Training training = createTestTraining(1L);
        trainingDAO.create(training);
        verify(mockStorage).put(1L, training);
    }

    @Test
    void testRead_ExistingTraining() {
        Training training = createTestTraining(1L);
        when(mockStorage.get(1L)).thenReturn(training);

        Optional<Training> result = trainingDAO.read(1L);

        assertTrue(result.isPresent());
        assertEquals(training, result.get());
        verify(mockStorage).get(1L);
    }

    @Test
    void testRead_NonExistingTraining() {
        when(mockStorage.get(1L)).thenReturn(null);

        Optional<Training> result = trainingDAO.read(1L);

        assertFalse(result.isPresent());
        verify(mockStorage).get(1L);
    }

    @Test
    void testUpdate_ExistingTraining() {
        Training training = createTestTraining(1L);
        when(mockStorage.containsKey(1L)).thenReturn(true);

        trainingDAO.update(training);

        verify(mockStorage).put(1L, training);
    }

    @Test
    void testUpdate_NonExistingTraining() {
        Training training = createTestTraining(1L);
        when(mockStorage.containsKey(1L)).thenReturn(false);

        trainingDAO.update(training);

        verify(mockStorage, never()).put(1L, training);
    }

    @Test
    void testDelete_ExistingTraining() {
        when(mockStorage.remove(1L)).thenReturn(createTestTraining(1L));

        trainingDAO.delete(1L);

        verify(mockStorage).remove(1L);
    }

    @Test
    void testDelete_NonExistingTraining() {
        when(mockStorage.remove(1L)).thenReturn(null);

        trainingDAO.delete(1L);

        verify(mockStorage).remove(1L);
    }

    @Test
    void testGetAll() {
        Training training1 = createTestTraining(1L);
        Training training2 = createTestTraining(2L);

        List<Training> trainings = Arrays.asList(training1, training2);
        when(mockStorage.values()).thenReturn(new HashSet<>(trainings));

        List<Training> result = trainingDAO.getAll();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(trainings));
    }

    private Training createTestTraining(Long id) {
        Training training = new Training();
        training.setId(id);
        training.setTrainingName("Java Basics");
        training.setTrainingType(TrainingType.YOGA);
        training.setTrainingDate(LocalDate.of(2024, 12, 18));
        training.setTrainingDuration(5);

        Trainee trainee = new Trainee();
        trainee.setId(10L);
        training.setTrainee(trainee);

        Trainer trainer = new Trainer();
        trainer.setId(20L);
        training.setTrainer(trainer);

        return training;
    }

}
