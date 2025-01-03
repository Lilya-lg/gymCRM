package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uz.gym.crm.dao.BaseDAO;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Training;
import uz.gym.crm.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbstractProfileServiceTest {

    private BaseDAO<Trainee> mockDao;
    private UserDAOImpl mockUserDAO;
    private TrainingDAOImpl mockTrainingDAO;
    private AbstractProfileService<Trainee> service;

    @BeforeEach
    void setUp() {
        mockDao = Mockito.mock(BaseDAO.class);
        mockUserDAO = Mockito.mock(UserDAOImpl.class);
        mockTrainingDAO = Mockito.mock(TrainingDAOImpl.class);

        // Anonymous subclass for testing
        service = new AbstractProfileService<>(mockDao, mockUserDAO) {
            @Override
            protected User getUser(Trainee entity) {
                return entity.getUser();
            }
        };
        ((AbstractProfileService<Trainee>) service).trainingDAO = mockTrainingDAO;
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

        when(mockUserDAO.findByUsernameAndPassword("johndoe", "password")).thenReturn(Optional.of(user));
        when(mockDao.findByUsername("johndoe")).thenReturn(Optional.of(existingTrainee));

        service.updateProfile("johndoe", "password", updatedTrainee);

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
        User user = new User();
        user.setUsername("johndoe");
        user.setActive(false);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(mockDao.findByUsername("johndoe")).thenReturn(Optional.of(trainee));

        service.activate("johndoe");

        assertTrue(user.isActive());
        verify(mockDao, times(1)).save(trainee);
    }

    @Test
    void deactivate_ShouldSetUserActiveToFalse_WhenAuthenticationSucceeds() {
        User user = new User();
        user.setUsername("johndoe");
        user.setPassword("password");
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(mockUserDAO.findByUsernameAndPassword("johndoe", "password")).thenReturn(Optional.of(user));
        when(mockDao.findByUsername("johndoe")).thenReturn(Optional.of(trainee));

        service.deactivate("johndoe", "password");

        assertFalse(user.isActive());
        verify(mockDao, times(1)).save(trainee);
    }

    @Test
    void getTrainingListByCriteria_ShouldReturnTrainingList_ForTrainee() {
        // Mocked User
        User user = new User();
        user.setUsername("johndoe");

        // Mocked Trainee
        Trainee trainee = new Trainee();
        trainee.setUser(user);

        // Mocked Trainings
        Training training1 = new Training();
        Training training2 = new Training();

        // Mock DAO behavior
        when(mockDao.findByUsername("johndoe")).thenReturn(Optional.of(trainee));
        when(mockTrainingDAO.findByCriteria(eq(trainee), any(), any(), any())).thenReturn(List.of(training1, training2));

        // Call the method under test
        List<Training> trainings = service.getTrainingListByCriteria("johndoe", LocalDate.now(), LocalDate.now().plusDays(1), null, null);

        // Verify results
        assertEquals(2, trainings.size());
        verify(mockDao, times(1)).findByUsername("johndoe");
        verify(mockTrainingDAO, times(1)).findByCriteria(eq(trainee), any(), any(), any());
    }
}


