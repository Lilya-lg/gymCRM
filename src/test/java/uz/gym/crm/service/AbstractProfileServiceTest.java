package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uz.gym.crm.dao.abstr.BaseDAO;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;
import uz.gym.crm.domain.User;
import uz.gym.crm.service.abstr.AbstractProfileService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbstractProfileServiceTest {

    private BaseDAO<Trainee> mockDao;
    private BaseDAO<Trainer> mockDao1;
    private UserDAOImpl mockUserDAO;
    private TrainingDAOImpl mockTrainingDAO;
    private AbstractProfileService<Trainee> service;


    @BeforeEach
    void setUp() {
        mockDao = Mockito.mock(BaseDAO.class);
        mockUserDAO = Mockito.mock(UserDAOImpl.class);
        mockTrainingDAO = Mockito.mock(TrainingDAOImpl.class);
        mockDao1 = Mockito.mock(BaseDAO.class);
        service = new AbstractProfileService<>(mockDao, mockUserDAO, mockTrainingDAO) {
            @Override
            protected User getUser(Trainee entity) {
                return entity.getUser();
            }
        };
    }

    @Test
    void prepareUser_ShouldGenerateUniqueUsernameAndPassword() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        when(mockUserDAO.getAll()).thenReturn(List.of());

        service.prepareUser(user);

        assertNotNull(user.getUsername());
        assertNotNull(user.getPassword());
    }

    @Test
    void updateProfile_ShouldUpdateEntity_WhenAuthenticationSucceeds() {
        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");

        User user = new User();
        user.setUsername("johndoe");
        user.setPassword("password");

        Trainee existingTrainee = new Trainee();
        existingTrainee.setId(1L);
        existingTrainee.setUser(user);

        Trainee updatedTrainee = new Trainee();
        updatedTrainee.setId(1L);
        User updatedUser = new User();
        updatedUser.setUsername("johndoe");
        updatedTrainee.setUser(updatedUser);
        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword"))
                .thenReturn(Optional.of(authenticatedUser));
        when(mockUserDAO.findByUsernameAndPassword("johndoe", "password")).thenReturn(Optional.of(user));
        when(mockDao.findByUsername("johndoe")).thenReturn(Optional.of(existingTrainee));

        service.updateProfile("adminUser", "adminPassword","johndoe", updatedTrainee);
        verify(mockUserDAO, times(1)).findByUsernameAndPassword("adminUser", "adminPassword");
        verify(mockDao, times(1)).save(existingTrainee);
        assertEquals("johndoe", existingTrainee.getUser().getUsername());
    }

    @Test
    void authenticate_ShouldReturnTrue_WhenCredentialsAreValid() {
        when(mockUserDAO.findByUsernameAndPassword("johndoe", "password")).thenReturn(Optional.of(new User()));

        boolean result = service.authenticate("johndoe", "password");

        assertTrue(result);
    }

    @Test
    void changePassword_ShouldUpdatePassword_WhenAuthenticationSucceeds() {
        User user = new User();
        user.setUsername("johndoe");
        user.setPassword("oldPassword");

        when(mockUserDAO.findByUsernameAndPassword("johndoe", "oldPassword")).thenReturn(Optional.of(user));
        when(mockUserDAO.findByUsername("johndoe")).thenReturn(Optional.of(user));

        service.changePassword("johndoe", "oldPassword", "newPassword");

        assertEquals("newPassword", user.getPassword());
        verify(mockUserDAO, times(1)).save(user);
    }

    @Test
    void activate_ShouldSetUserActiveToTrue() {
        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");

        User userToActivate = new User();
        userToActivate.setUsername("johndoe");
        userToActivate.setActive(false);

        Trainee traineeToActivate = new Trainee();
        traineeToActivate.setUser(userToActivate);

        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword"))
                .thenReturn(Optional.of(authenticatedUser));


        when(mockDao.findByUsername("johndoe")).thenReturn(Optional.of(traineeToActivate));


        service.activate("adminUser", "johndoe", "adminPassword");


        verify(mockUserDAO, times(1)).findByUsernameAndPassword("adminUser", "adminPassword");
        verify(mockDao, times(1)).findByUsername("johndoe");
        verify(mockDao, times(1)).save(traineeToActivate);

        assertTrue(userToActivate.isActive(), "The user should be activated.");
    }

    @Test
    void deactivate_ShouldSetUserActiveToFalse_WhenAuthenticationSucceeds() {
        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");

        User user = new User();
        user.setUsername("johndoe");
        user.setPassword("password");
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword"))
                .thenReturn(Optional.of(authenticatedUser));


        when(mockDao.findByUsername("johndoe")).thenReturn(Optional.of(trainee));


        service.deactivate("adminUser", "johndoe", "adminPassword");
        verify(mockUserDAO, times(1)).findByUsernameAndPassword("adminUser", "adminPassword");
        verify(mockDao, times(1)).findByUsername("johndoe");
        verify(mockDao, times(1)).save(trainee);

        assertFalse(user.isActive());
        verify(mockDao, times(1)).save(trainee);
    }

    @Test
    void getTrainingListByCriteria_ShouldReturnTrainingList_ForTrainee() {
        User authenticatedUser = new User();
        authenticatedUser.setUsername("adminUser");
        authenticatedUser.setPassword("adminPassword");

        User user = new User();
        user.setUsername("traineeJohn");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        Training training1 = new Training();
        Training training2 = new Training();

        when(mockUserDAO.findByUsernameAndPassword("adminUser", "adminPassword"))
                .thenReturn(Optional.of(authenticatedUser));
        when(mockDao.findByUsername("traineeJohn")).thenReturn(Optional.of(trainee));
        when(mockTrainingDAO.findByCriteria("traineeJohn", "Yoga", LocalDate.now(), LocalDate.now().plusDays(1), "trainerJohn")).thenReturn(List.of(training1, training2));

        List<Training> trainings = service.getTrainingListByCriteria("traineeJohn", LocalDate.now(), LocalDate.now().plusDays(1), "trainerJohn", "Yoga", null,"adminUser","adminPassword");

        assertEquals(2, trainings.size());
        verify(mockUserDAO, times(1)).findByUsernameAndPassword("adminUser", "adminPassword");
        verify(mockDao, times(1)).findByUsername("traineeJohn");
        verify(mockTrainingDAO, times(1)).findByCriteria("traineeJohn", "Yoga", LocalDate.now(), LocalDate.now().plusDays(1), "trainerJohn");
    }

}


