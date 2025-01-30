package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uz.gym.crm.dao.abstr.BaseDAO;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.User;
import uz.gym.crm.service.abstr.AbstractProfileService;

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

        service = new AbstractProfileService<>(mockDao, mockUserDAO, mockTrainingDAO) {
            @Override
            protected User getUser(Trainee entity) {
                return entity.getUser();
            }
        };
    }

    @Test
    void activate_ShouldSetUserActiveToTrue() {

        User userToActivate = new User();
        userToActivate.setUsername("johndoe");
        userToActivate.setIsActive(false);

        Trainee traineeToActivate = new Trainee();
        traineeToActivate.setUser(userToActivate);

        when(mockDao.findByUsername("johndoe")).thenReturn(Optional.of(traineeToActivate));
        when(mockUserDAO.findByUsername("johndoe")).thenReturn(Optional.of(userToActivate)); // Mock this as well


        service.activate("johndoe");


        verify(mockUserDAO, times(1)).findByUsername("johndoe");
        verify(mockUserDAO, times(1)).updateUser(userToActivate);
        assertTrue(userToActivate.getIsActive(), "The user should be activated.");
    }

    @Test
    void deactivate_ShouldSetUserActiveToFalse() {

        User userToDeactivate = new User();
        userToDeactivate.setUsername("johndoe");
        userToDeactivate.setIsActive(true);

        Trainee traineeToDeactivate = new Trainee();
        traineeToDeactivate.setUser(userToDeactivate);

        when(mockUserDAO.findByUsername("johndoe")).thenReturn(Optional.of(userToDeactivate));
        when(mockUserDAO.findByUsername("johndoe")).thenReturn(Optional.of(userToDeactivate)); // Mock this as well


        service.deactivate("johndoe");


        verify(mockUserDAO, times(1)).findByUsername("johndoe");
        verify(mockUserDAO, times(1)).updateUser(userToDeactivate);
        assertFalse(userToDeactivate.getIsActive(), "The user should be deactivated.");
    }

    @Test
    void changePassword_ShouldUpdatePassword() {

        User user = new User();
        user.setUsername("johndoe");
        user.setPassword("oldPassword");

        when(mockUserDAO.findByUsernameAndPassword("johndoe", "oldPassword")).thenReturn(Optional.of(user));


        service.changePassword("johndoe", "oldPassword", "newPassword");


        verify(mockUserDAO, times(1)).findByUsernameAndPassword("johndoe", "oldPassword");
        verify(mockUserDAO, times(1)).updateUser(user);
        assertEquals("newPassword", user.getPassword(), "The password should be updated.");
    }
    @Test
    void authenticate_ShouldReturnTrue_WhenCredentialsAreValid() {

        when(mockUserDAO.findByUsernameAndPassword("johndoe", "password"))
                .thenReturn(Optional.of(new User()));


        boolean result = service.authenticate("johndoe", "password");


        assertTrue(result, "Authentication should return true for valid credentials.");
        verify(mockUserDAO, times(1)).findByUsernameAndPassword("johndoe", "password");
    }

    @Test
    void authenticate_ShouldReturnFalse_WhenCredentialsAreInvalid() {

        when(mockUserDAO.findByUsernameAndPassword("johndoe", "wrongpassword"))
                .thenReturn(Optional.empty());


        boolean result = service.authenticate("johndoe", "wrongpassword");

        assertFalse(result, "Authentication should return false for invalid credentials.");
        verify(mockUserDAO, times(1)).findByUsernameAndPassword("johndoe", "wrongpassword");
    }

    @Test
    void prepareUser_ShouldGenerateUniqueUsernameAndPassword() {

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        when(mockUserDAO.getAll()).thenReturn(List.of());

        service.prepareUser(user);

        assertNotNull(user.getUsername(), "Username should be generated.");
        assertNotNull(user.getPassword(), "Password should be generated.");
    }
}
