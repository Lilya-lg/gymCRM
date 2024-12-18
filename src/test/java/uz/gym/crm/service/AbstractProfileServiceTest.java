package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.gym.crm.dao.TraineeDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainee;
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
    void testPrepareUserWithMissingUsername() {
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setFirstName("John"); // Ensure required fields are set
        user.setLastName("Doe");

        List<User> existingUsers = Arrays.asList(createTestUser(2L));
        when(userDAO.getAll()).thenReturn(existingUsers);

        // Act
        profileService.prepareUser(user);

        // Assert
        assertNotNull(user.getUsername(), "Username should not be null");
        assertTrue(user.getUsername().startsWith("john.doe"), "Username should start with 'john.doe'");
        verify(userDAO).getAll();
    }

    @Test
    void testPrepareUserWithMissingPassword() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        profileService.prepareUser(user);

        assertNotNull(user.getPassword());
    }

    @Test
    void testGetUserFromEntity() {
        Trainee trainee = new Trainee();
        trainee.setUserId(100L);
        User user = createTestUser(100L);

        // Ensure proper mock setup
        when(userDAO.read(100L)).thenReturn(Optional.of(user)); // Use 'read' instead of 'findById'

        // Act
        User result = profileService.getUser(trainee);

        // Assert
        assertNotNull(result, "User should not be null");
        assertEquals(user, result, "Returned user should match the mocked user");
        verify(userDAO).read(100L); // Verify interaction with mock

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

    // Concrete subclass for testing purposes
    private static class TestProfileService extends AbstractProfileService<Trainee> {

        public TestProfileService(TraineeDAOImpl traineeDAO, UserDAOImpl userDAO) {
            super(traineeDAO, userDAO);
        }

        @Override
        protected User getUser(Trainee entity) {
            return userDAO.read(entity.getUserId()).orElseThrow(() ->
                    new IllegalArgumentException("User not found for ID: " + entity.getUserId()));
        }
    }
}
