package uz.gym.crm.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.gym.crm.dao.TrainerDAO;
import uz.gym.crm.dao.TrainerDAOImpl;
import uz.gym.crm.dao.UserDAO;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceTest {
    private TrainerDAO trainerDAO;
    private UserDAO userDAO;
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerDAO = mock(TrainerDAO.class);
        userDAO = mock(UserDAO.class);
        trainerService = new TrainerServiceImpl(trainerDAO, userDAO);
    }

    @Test
    void testCreateTrainer() {

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setUsername("john.doe");
        mockUser.setPassword("password123");

        when(userDAO.findById(1)).thenReturn(Optional.of(mockUser));


        doNothing().when(trainerDAO).create(any(Trainer.class));

        Trainer trainer = new Trainer();
        trainer.setId(1);
        trainer.setSpecialization("Yoga");
        trainer.setUserId(1);

        trainerService.create(trainer); // Save the trainer

        // Verify interactions
        verify(userDAO).findById(1);
        verify(trainerDAO).create(trainer);

        // Validate trainer properties
        assertEquals(1, trainer.getUserId(), "Trainer's userId should match the resolved user's ID.");
    }

    @Test
    void testCreateTrainerWithNonExistentUser() {
        when(userDAO.findById(99)).thenReturn(Optional.empty()); // Simulate user not found

        Trainer trainer = new Trainer();
        trainer.setId(1);
        trainer.setSpecialization("Yoga");
        trainer.setUserId(99);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> trainerService.create(trainer));
        assertEquals("User not found for ID: 99", exception.getMessage());

        verify(userDAO).findById(99);
        verifyNoInteractions(trainerDAO);
    }

    @Test
    void testGetAllTrainers() {

        List<Trainer> mockStorage = new ArrayList<>();
        when(trainerDAO.getAll()).thenReturn(mockStorage);

        doAnswer(invocation -> {
            Trainer trainer = invocation.getArgument(0);
            mockStorage.add(trainer);
            return null;
        }).when(trainerDAO).create(any(Trainer.class));


        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        when(userDAO.findById(1)).thenReturn(Optional.of(mockUser));


        Trainer trainer = new Trainer();
        trainer.setId(1);
        trainer.setSpecialization("Yoga");
        trainer.setUserId(1);

        trainerService.create(trainer);


        List<Trainer> trainers = trainerService.getAll();
        assertFalse(trainers.isEmpty(), "Trainers list should not be empty.");
        assertEquals(1, trainers.size(), "Trainers list size should be 1.");
        assertEquals(trainer, trainers.get(0), "Retrieved trainer should match the created trainer.");
    }
}
