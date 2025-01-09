package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uz.gym.crm.dao.TrainerDAOImpl;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    private UserDAOImpl mockUserDAO;
    private TrainerDAOImpl mockTrainerDAO;
    private TrainerServiceImpl service;
    private TrainingDAOImpl mockTrainingDAO;


    @BeforeEach
    void setUp() {
        mockUserDAO = Mockito.mock(UserDAOImpl.class);
        mockTrainerDAO = Mockito.mock(TrainerDAOImpl.class);
        mockTrainingDAO = Mockito.mock(TrainingDAOImpl.class);
        service = new TrainerServiceImpl(mockUserDAO, mockTrainerDAO, mockTrainingDAO);
    }

    @Test
    void create_ShouldPrepareUserAndSaveTrainer() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        doNothing().when(mockTrainerDAO).save(any(Trainer.class));

        service.create(trainer);

        assertNotNull(user.getUsername());
        assertNotNull(user.getPassword());
        verify(mockTrainerDAO, times(1)).save(trainer);
    }

    @Test
    void findByUsernameAndPassword_ShouldReturnTrainer_WhenCredentialsAreValid() {
        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");
        Trainer trainer = new Trainer();
        trainer.setId(1L);

        when(mockTrainerDAO.findByUsernameAndPassword("johndoe", "password")).thenReturn(Optional.of(trainer));

        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword"))
                .thenReturn(Optional.of(authenticatedUser));
        Optional<Trainer> result = service.findByUsernameAndPassword("adminUser", "adminPassword","johndoe", "password");

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(mockTrainerDAO, times(1)).findByUsernameAndPassword("johndoe", "password");
    }

    @Test
    void findByUsernameAndPassword_ShouldThrowException_WhenErrorOccurs() {
        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");
        when(mockTrainerDAO.findByUsernameAndPassword("johndoe", "password")).thenThrow(new RuntimeException("Database error"));

        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword"))
                .thenReturn(Optional.of(authenticatedUser));
        Exception exception = assertThrows(RuntimeException.class, () -> service.findByUsernameAndPassword("adminUser", "adminPassword","johndoe", "password"));

        assertEquals("Database error", exception.getMessage());
        verify(mockTrainerDAO, times(1)).findByUsernameAndPassword("johndoe", "password");
    }

    @Test
    void create_ShouldLogSuccessMessage() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        doNothing().when(mockTrainerDAO).save(any(Trainer.class));

        service.create(trainer);

        assertNotNull(user.getUsername());
        assertNotNull(user.getPassword());
        verify(mockTrainerDAO, times(1)).save(trainer);
    }

    @Test
    void findByUsername_ShouldReturnTrainer() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);

        when(mockTrainerDAO.findByUsername("johndoe")).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = service.findByUsername("johndoe");

        assertTrue(result.isPresent(), "Trainer should be found");
        assertEquals(1L, result.get().getId());
        verify(mockTrainerDAO, times(1)).findByUsername("johndoe");
    }

    @Test
    void findByUsername_ShouldReturnEmptyOptional() {
        when(mockTrainerDAO.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<Trainer> result = service.findByUsername("nonexistent");

        assertTrue(result.isEmpty(), "No trainer should be found for invalid username");
        verify(mockTrainerDAO, times(1)).findByUsername("nonexistent");
    }

    @Test
    void getUnassignedTrainersForTrainee_ShouldReturnListOfTrainers() {
        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");

        Trainer trainer1 = new Trainer();
        trainer1.setId(1L);
        Trainer trainer2 = new Trainer();
        trainer2.setId(2L);
        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword"))
                .thenReturn(Optional.of(authenticatedUser));

        when(mockTrainerDAO.getUnassignedTrainersByTraineeUsername("trainee1")).thenReturn(List.of(trainer1, trainer2));

        List<Trainer> result = service.getUnassignedTrainersForTrainee("trainee1","adminUser", "adminPassword");

        assertEquals(2, result.size(), "Should return two unassigned trainers");
        assertTrue(result.contains(trainer1) && result.contains(trainer2), "Returned trainers should match expected");
        verify(mockTrainerDAO, times(1)).getUnassignedTrainersByTraineeUsername("trainee1");
        verify(mockUserDAO, times(1)).findByUsernameAndPassword("adminUser", "adminPassword");
    }

    @Test
    void getUnassignedTrainersForTrainee_ShouldReturnEmptyList() {
        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");
        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword"))
                .thenReturn(Optional.of(authenticatedUser));
        when(mockTrainerDAO.getUnassignedTrainersByTraineeUsername("trainee1")).thenReturn(List.of());

        List<Trainer> result = service.getUnassignedTrainersForTrainee("trainee1","adminUser", "adminPassword");

        assertTrue(result.isEmpty(), "No trainers should be found for unassigned trainers query");
        verify(mockTrainerDAO, times(1)).getUnassignedTrainersByTraineeUsername("trainee1");
    }
}

