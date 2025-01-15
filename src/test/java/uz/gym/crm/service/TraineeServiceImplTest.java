package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uz.gym.crm.dao.TraineeDAOImpl;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TraineeServiceImplTest {
    private UserDAOImpl mockUserDAO;
    private TraineeDAOImpl mockTraineeDAO;
    private TrainingDAOImpl mockTrainingDAO;
    private TraineeServiceImpl service;

    @BeforeEach
    void setUp() {
        mockUserDAO = Mockito.mock(UserDAOImpl.class);
        mockTraineeDAO = Mockito.mock(TraineeDAOImpl.class);
        mockTrainingDAO = Mockito.mock(TrainingDAOImpl.class);
        service = new TraineeServiceImpl(mockUserDAO, mockTraineeDAO, mockTrainingDAO);
    }

    @Test
    void create_ShouldPrepareUserAndSaveTrainee() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        doNothing().when(mockTraineeDAO).save(any(Trainee.class));

        service.create(trainee);

        assertNotNull(user.getUsername());
        assertNotNull(user.getPassword());
        verify(mockTraineeDAO, times(1)).save(trainee);
    }

    @Test
    void deleteProfileByUsername_ShouldDeleteTraineeAndUser() {
        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");

        User user = new User();
        user.setUsername("johndoe");
        user.setPassword("password");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);
        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword")).thenReturn(Optional.of(authenticatedUser));
        when(mockUserDAO.findByUsernameAndPassword("johndoe", "password")).thenReturn(Optional.of(user));
        when(mockTraineeDAO.findByUsername("johndoe")).thenReturn(Optional.of(trainee));

        service.deleteProfileByUsername("adminUser", "johndoe", "adminPassword");
        verify(mockUserDAO, times(1)).findByUsernameAndPassword("adminUser", "adminPassword");

    }

    @Test
    void findByUsernameAndPassword_ShouldReturnTrainee_WhenCredentialsAreValid() {
        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword"))
                .thenReturn(Optional.of(authenticatedUser));
        when(mockTraineeDAO.findByUsernameAndPassword("johndoe", "password")).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = service.findByUsernameAndPassword("adminUser", "adminPassword", "johndoe", "password");

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(mockTraineeDAO, times(1)).findByUsernameAndPassword("johndoe", "password");
        verify(mockUserDAO, times(1)).findByUsernameAndPassword("adminUser", "adminPassword");
    }

    @Test
    void findByUsernameAndPassword_ShouldThrowException_WhenCredentialsAreInvalid() {
        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");
        when(mockTraineeDAO.findByUsernameAndPassword("johndoe", "wrongpassword")).thenReturn(Optional.empty());

        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword"))
                .thenReturn(Optional.of(authenticatedUser));
        Optional<Trainee> result = service.findByUsernameAndPassword("adminUser", "adminPassword", "johndoe", "wrongpassword");

        assertFalse(result.isPresent());
    }

    @Test
    void updateTraineeTrainerList_ShouldUpdateTrainerList_WhenAuthenticated() {

        String username = "adminUser";
        String password = "adminPassword";
        Long traineeId = 1L;
        List<Long> trainerIds = List.of(101L, 102L);

        User authenticatedUser = new User();
        authenticatedUser.setUsername(username);
        authenticatedUser.setPassword(password);

        Trainee trainee = new Trainee();
        trainee.setId(traineeId);
        trainee.setUser(authenticatedUser);

        when(mockUserDAO.findByUsernameAndPassword(username, password)).thenReturn(Optional.of(authenticatedUser));

        doNothing().when(mockTraineeDAO).updateTraineeTrainerList(traineeId, trainerIds);


        service.updateTraineeTrainerList(username, password, traineeId, trainerIds);


        verify(mockUserDAO, times(1)).findByUsernameAndPassword(username, password);
        verify(mockTraineeDAO, times(1)).updateTraineeTrainerList(traineeId, trainerIds);
    }

    @Test
    void updateTraineeTrainerList_ShouldThrowException_WhenNotAuthenticated() {

        String username = "adminUser";
        String password = "wrongPassword";
        Long traineeId = 1L;
        List<Long> trainerIds = List.of(101L, 102L);

        when(mockUserDAO.findByUsernameAndPassword(username, password)).thenReturn(Optional.empty());


        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.updateTraineeTrainerList(username, password, traineeId, trainerIds));

        assertEquals("Invalid username or password.", exception.getMessage());
        verify(mockUserDAO, times(1)).findByUsernameAndPassword(username, password);
        verify(mockTraineeDAO, never()).updateTraineeTrainerList(traineeId, trainerIds);
    }

    @Test
    void findByUsername_ShouldReturnTrainee_WhenAuthenticated() {

        String usernameAuth = "adminUser";
        String passwordAuth = "adminPassword";
        String usernameToSelect = "traineeJane";

        User authenticatedUser = new User();
        authenticatedUser.setUsername(usernameAuth);
        authenticatedUser.setPassword(passwordAuth);

        Trainee trainee = new Trainee();
        trainee.setId(1L);

        when(mockUserDAO.findByUsernameAndPassword(usernameAuth, passwordAuth)).thenReturn(Optional.of(authenticatedUser));
        when(mockTraineeDAO.findByUsername(usernameToSelect)).thenReturn(Optional.of(trainee));


        Optional<Trainee> result = service.findByUsername(usernameAuth, passwordAuth, usernameToSelect);


        assertTrue(result.isPresent(), "Trainee should be found");
        assertEquals(1L, result.get().getId(), "Trainee ID should match");
        verify(mockUserDAO, times(1)).findByUsernameAndPassword(usernameAuth, passwordAuth);
        verify(mockTraineeDAO, times(1)).findByUsername(usernameToSelect);
    }

    @Test
    void findByUsername_ShouldThrowException_WhenNotAuthenticated() {

        String usernameAuth = "adminUser";
        String passwordAuth = "wrongPassword";
        String usernameToSelect = "traineeJane";

        when(mockUserDAO.findByUsernameAndPassword(usernameAuth, passwordAuth)).thenReturn(Optional.empty());


        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.findByUsername(usernameAuth, passwordAuth, usernameToSelect)
        );

        assertEquals("Invalid username or password.", exception.getMessage());
        verify(mockUserDAO, times(1)).findByUsernameAndPassword(usernameAuth, passwordAuth);
        verify(mockTraineeDAO, never()).findByUsername(anyString());
    }

    @Test
    void findByUsernameAndPassword_ShouldReturnTrainee_WhenAuthenticated() {

        String usernameAuth = "adminUser";
        String passwordAuth = "adminPassword";
        String username = "traineeJane";
        String password = "password123";

        User authenticatedUser = new User();
        authenticatedUser.setUsername(usernameAuth);
        authenticatedUser.setPassword(passwordAuth);

        Trainee trainee = new Trainee();
        trainee.setId(1L);

        when(mockUserDAO.findByUsernameAndPassword(usernameAuth, passwordAuth)).thenReturn(Optional.of(authenticatedUser));
        when(mockTraineeDAO.findByUsernameAndPassword(username, password)).thenReturn(Optional.of(trainee));


        Optional<Trainee> result = service.findByUsernameAndPassword(usernameAuth, passwordAuth, username, password);


        assertTrue(result.isPresent(), "Trainee should be found");
        assertEquals(1L, result.get().getId(), "Trainee ID should match");
        verify(mockUserDAO, times(1)).findByUsernameAndPassword(usernameAuth, passwordAuth);
        verify(mockTraineeDAO, times(1)).findByUsernameAndPassword(username, password);
    }

    @Test
    void findByUsernameAndPassword_ShouldThrowException_WhenNotAuthenticated() {

        String usernameAuth = "adminUser";
        String passwordAuth = "wrongPassword";
        String username = "traineeJane";
        String password = "password123";

        when(mockUserDAO.findByUsernameAndPassword(usernameAuth, passwordAuth)).thenReturn(Optional.empty());


        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.findByUsernameAndPassword(usernameAuth, passwordAuth, username, password)
        );

        assertEquals("Invalid username or password.", exception.getMessage());
        verify(mockUserDAO, times(1)).findByUsernameAndPassword(usernameAuth, passwordAuth);
        verify(mockTraineeDAO, never()).findByUsernameAndPassword(anyString(), anyString());
    }
}