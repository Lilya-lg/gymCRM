package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.gym.crm.dao.BaseDAO;
import uz.gym.crm.dao.TraineeDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;
import uz.gym.crm.util.UsernameGenerator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AbstractProfileServiceTest {
    @InjectMocks
    private TestProfileService profileService;

    @Mock
    private TraineeDAOImpl traineeDAO;

    @Mock
    private UserDAOImpl userDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        profileService = new TestProfileService(traineeDAO, userDAO);
    }





    @Test
    void testPrepareUserWithMissingPassword() {
        Trainer user = new Trainer();
        user.setId(1L);
        user.setUsername("testuser");

        profileService.prepareUser(user);

        assertNotNull(user.getPassword());
    }

    @Test
    void testGetUserFromEntity() {
        Trainee trainee = new Trainee();
        trainee.setId(100L); // Используем id, унаследованный от User
        trainee.setFirstName("Jane");
        trainee.setLastName("Doe");

        // Мокируем возврат из userDAO
        when(userDAO.read(100L)).thenReturn(Optional.of(trainee));

        // Act
        User result = profileService.getUser(trainee);

        // Assert
        assertNotNull(result, "User should not be null");
        assertEquals(trainee, result, "Returned user should match the mocked user");
        verify(userDAO).read(100L); // Убедитесь, что userDAO вызван

    }


    private User createTestUser(Long id) {
        Trainer user = new Trainer();
        user.setId(id);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setPassword("password");
        user.setActive(true);
        return user;
    }

    // Concrete subclass for testing purposes
    private static class TestProfileService extends AbstractProfileService<Trainee> {
        private final BaseDAO<User> userDAO;
        public TestProfileService(BaseDAO<Trainee> traineeDAO, BaseDAO<User> userDAO) {
            super(traineeDAO,userDAO);
            this.userDAO = userDAO;
        }


        @Override
        protected User getUser(Trainee entity) {
            return userDAO.read(entity.getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found for ID: " + entity.getId()));
        }
    }
}
