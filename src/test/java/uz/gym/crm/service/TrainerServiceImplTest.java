package uz.gym.crm.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.gym.crm.dao.TrainerDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Mock
    private TrainerDAOImpl trainerDAO;

    @Mock
    private UserDAOImpl userDAO;

    @BeforeEach
        void setUp() {
        MockitoAnnotations.openMocks(this);
        trainerService = new TrainerServiceImpl(trainerDAO, userDAO);
    }

    @Test
    void testCreateTrainerWithExistingUser() {
        Trainer trainer = createTestTrainer(1L); // Trainer with userId = 100L
        User user = createTestUser(trainer.getUserId()); // User with id = 100L
        when(userDAO.read(100L)).thenReturn(Optional.of(user)); // Mock read method

        // Act
        trainerService.create(trainer);

        // Assert
        verify(userDAO).read(100L); // Verify userDAO.read is called with 100L
        verify(trainerDAO).create(trainer); // Verify trainerDAO.create is called

    }

    @Test
    void testCreateTrainerWithNonExistingUser() {
        Trainer trainer = createTestTrainer(1L);
        when(userDAO.read(trainer.getUserId())).thenReturn(Optional.empty()); // Use 'read' instead of 'findById'

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            trainerService.create(trainer);
        });

        assertEquals("User not found for ID: " + trainer.getUserId(), exception.getMessage());
        verify(userDAO).read(trainer.getUserId()); // Verify 'read' is called
        verify(trainerDAO, never()).create(trainer); // Ensure 'create' is not called

    }

    @Test
    void testGetAllTrainers() {
        Trainer trainer1 = createTestTrainer(1L);
        Trainer trainer2 = createTestTrainer(2L);
        List<Trainer> trainers = Arrays.asList(trainer1, trainer2);

        when(trainerDAO.getAll()).thenReturn(trainers);

        List<Trainer> result = trainerService.getAll();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(trainers));
        verify(trainerDAO).getAll();
    }

    private Trainer createTestTrainer(Long id) {
        Trainer trainer = new Trainer();
        trainer.setId(id);
        trainer.setSpecialization("Java Trainer");
        trainer.setUserId(100L);
        return trainer;
    }

    private User createTestUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setPassword("password");
        user.setActive(true);
        return user;
    }

}
