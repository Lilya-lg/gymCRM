package uz.gym.crm.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.gym.crm.domain.Trainee;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TraineeDAOImplTest {

    private TraineeDAOImpl traineeDAO;

    @Mock
    private Map<Long, Trainee> mockStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        traineeDAO = new TraineeDAOImpl(mockStorage);
    }

    @Test
    void testCreate() {
        Trainee trainee = createTestTrainee(1L);
        traineeDAO.create(trainee);
        verify(mockStorage).put(1L, trainee);
    }

    @Test
    void testRead_ExistingTrainee() {
        Trainee trainee = createTestTrainee(1L);
        when(mockStorage.get(1L)).thenReturn(trainee);

        Optional<Trainee> result = traineeDAO.read(1L);

        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());
        verify(mockStorage).get(1L);
    }

    @Test
    void testRead_NonExistingTrainee() {
        when(mockStorage.get(1L)).thenReturn(null);

        Optional<uz.gym.crm.domain.Trainee> result = traineeDAO.read(1L);

        assertFalse(result.isPresent());
        verify(mockStorage).get(1L);
    }

    @Test
    void testUpdate_ExistingTrainee() {
        Trainee trainee = createTestTrainee(1L);
        when(mockStorage.containsKey(1L)).thenReturn(true);

        traineeDAO.update(trainee);

        verify(mockStorage).put(1L, trainee);
    }

    @Test
    void testUpdate_NonExistingTrainee() {
        Trainee trainee = createTestTrainee(1L);
        when(mockStorage.containsKey(1L)).thenReturn(false);

        traineeDAO.update(trainee);

        verify(mockStorage, never()).put(1L, trainee);
    }

    @Test
    void testDelete_ExistingTrainee() {
        when(mockStorage.remove(1L)).thenReturn(createTestTrainee(1L));

        traineeDAO.delete(1L);

        verify(mockStorage).remove(1L);
    }

    @Test
    void testDelete_NonExistingTrainee() {
        when(mockStorage.remove(1L)).thenReturn(null);

        traineeDAO.delete(1L);

        verify(mockStorage).remove(1L);
    }

    @Test
    void testGetAll() {
        Trainee trainee1 = createTestTrainee(1L);
        Trainee trainee2 = createTestTrainee(2L);

        List<Trainee> trainees = Arrays.asList(trainee1, trainee2);
        when(mockStorage.values()).thenReturn(new HashSet<>(trainees));

        List<Trainee> result = traineeDAO.getAll();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(trainees));
    }

    private Trainee createTestTrainee(Long id) {
        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        trainee.setAddress("123 Test St");
        return trainee;
    }
}
