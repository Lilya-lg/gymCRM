package uz.gym.crm.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.MockitoAnnotations;
import uz.gym.crm.domain.Trainer;

import java.util.*;


class TrainerDAOImplTest {

    private TrainerDAOImpl trainerDAO;

    @Mock
    private Map<Long, Trainer> mockStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainerDAO = new TrainerDAOImpl(mockStorage);
    }

    @Test
    void testCreate() {
        Trainer trainer = createTestTrainer(1L);
        trainerDAO.create(trainer);
        verify(mockStorage).put(1L, trainer);
    }

    @Test
    void testRead_ExistingTrainer() {
        Trainer trainer = createTestTrainer(1L);
        when(mockStorage.get(1L)).thenReturn(trainer);

        Optional<Trainer> result = trainerDAO.read(1L);

        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());
        verify(mockStorage).get(1L);
    }

    @Test
    void testRead_NonExistingTrainer() {
        when(mockStorage.get(1L)).thenReturn(null);

        Optional<Trainer> result = trainerDAO.read(1L);

        assertFalse(result.isPresent());
        verify(mockStorage).get(1L);
    }

    @Test
    void testUpdate_ExistingTrainer() {
        Trainer trainer = createTestTrainer(1L);
        when(mockStorage.containsKey(1L)).thenReturn(true);

        trainerDAO.update(trainer);

        verify(mockStorage).put(1L, trainer);
    }

    @Test
    void testUpdate_NonExistingTrainer() {
        Trainer trainer = createTestTrainer(1L);
        when(mockStorage.containsKey(1L)).thenReturn(false);

        trainerDAO.update(trainer);

        verify(mockStorage, never()).put(1L, trainer);
    }

    @Test
    void testDelete_ExistingTrainer() {
        when(mockStorage.remove(1L)).thenReturn(createTestTrainer(1L));

        trainerDAO.delete(1L);

        verify(mockStorage).remove(1L);
    }

    @Test
    void testDelete_NonExistingTrainer() {
        when(mockStorage.remove(1L)).thenReturn(null);

        trainerDAO.delete(1L);

        verify(mockStorage).remove(1L);
    }

    @Test
    void testGetAll() {
        Trainer trainer1 = createTestTrainer(1L);
        Trainer trainer2 = createTestTrainer(2L);

        List<Trainer> trainers = Arrays.asList(trainer1, trainer2);
        when(mockStorage.values()).thenReturn(new HashSet<>(trainers));

        List<Trainer> result = trainerDAO.getAll();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(trainers));
    }

    private Trainer createTestTrainer(Long id) {
        Trainer trainer = new Trainer();
        trainer.setId(id);
        trainer.setSpecialization("Java Trainer");
        return trainer;
    }
}
