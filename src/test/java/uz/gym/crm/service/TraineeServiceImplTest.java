package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uz.gym.crm.dao.TraineeDAOImpl;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;
import uz.gym.crm.dto.TraineeProfileDTO;
import uz.gym.crm.dto.TraineeUpdateDTO;
import uz.gym.crm.mapper.Mapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplTest {

    private UserDAOImpl mockUserDAO;
    private TraineeDAOImpl mockTraineeDAO;
    private TrainingDAOImpl mockTrainingDAO;
    private Mapper mockMapper;
    private TraineeServiceImpl service;

    @BeforeEach
    void setUp() {
        mockUserDAO = Mockito.mock(UserDAOImpl.class);
        mockTraineeDAO = Mockito.mock(TraineeDAOImpl.class);
        mockTrainingDAO = Mockito.mock(TrainingDAOImpl.class);
        mockMapper = Mockito.mock(Mapper.class);
        service = new TraineeServiceImpl(mockUserDAO, mockTraineeDAO, mockTrainingDAO, mockMapper);
    }

    @Test
    void create_ShouldPrepareUserAndSaveTrainee() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        doNothing().when(mockUserDAO).save(user);
        doNothing().when(mockTraineeDAO).save(any(Trainee.class));

        service.create(trainee);

        assertNotNull(user.getUsername(), "Username should not be null");
        assertNotNull(user.getPassword(), "Password should not be null");
        verify(mockUserDAO, times(1)).save(user);
        verify(mockTraineeDAO, times(1)).save(trainee);
    }

    @Test
    void deleteProfileByUsername_ShouldDeleteTraineeAndUser() {
        String username = "johndoe";
        doNothing().when(mockTraineeDAO).deleteByUsername(username);

        service.deleteProfileByUsername(username);

        verify(mockTraineeDAO, times(1)).deleteByUsername(username);
    }

    @Test
    void findByUsernameAndPassword_ShouldReturnTrainee_WhenCredentialsAreValid() {
        String usernameAuth = "adminUser";
        String passwordAuth = "adminPassword";
        String username = "johndoe";
        String password = "password";

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
        String username = "johndoe";
        String password = "password";

        when(mockUserDAO.findByUsernameAndPassword(usernameAuth, passwordAuth)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.findByUsernameAndPassword(usernameAuth, passwordAuth, username, password)
        );

        assertEquals("Invalid username or password.", exception.getMessage());
        verify(mockUserDAO, times(1)).findByUsernameAndPassword(usernameAuth, passwordAuth);
        verify(mockTraineeDAO, never()).findByUsernameAndPassword(anyString(), anyString());
    }

    @Test
    void updateTraineeTrainerList_ShouldUpdateTrainerList() {
        String username = "traineeUser";
        List<String> trainerIds = List.of("101", "102");

        Trainee trainee = new Trainee();
        trainee.setId(1L);

        when(mockTraineeDAO.findByUsername(username)).thenReturn(Optional.of(trainee));
        doNothing().when(mockTraineeDAO).updateTraineeTrainerList(trainee.getId(), trainerIds);
        when(mockTrainingDAO.findTrainersByTraineeId(trainee.getId())).thenReturn(List.of(new Trainer()));

        List<Trainer> trainers = service.updateTraineeTrainerList(username, trainerIds);

        assertNotNull(trainers, "Trainers should not be null");
        verify(mockTraineeDAO, times(1)).findByUsername(username);
        verify(mockTraineeDAO, times(1)).updateTraineeTrainerList(trainee.getId(), trainerIds);
        verify(mockTrainingDAO, times(1)).findTrainersByTraineeId(trainee.getId());
    }

    @Test
    void getTraineeProfile_ShouldReturnProfileDTO() {
        String username = "traineeUser";
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setActive(true);
        trainee.setUser(user);

        when(mockTraineeDAO.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(mockTrainingDAO.findTrainersByTraineeId(1L)).thenReturn(List.of());

        TraineeProfileDTO profile = service.getTraineeProfile(username);

        assertNotNull(profile, "Profile should not be null");
        assertEquals("John", profile.getFirstName(), "First name should match");
        assertEquals("Doe", profile.getSecondName(), "Second name should match");
        verify(mockTraineeDAO, times(1)).findByUsername(username);
        verify(mockTrainingDAO, times(1)).findTrainersByTraineeId(1L);
    }
}
