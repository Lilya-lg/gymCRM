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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TraineeServiceImplTest {

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Mock
    private TraineeDAOImpl traineeDAO;

    @Mock
    private UserDAOImpl userDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        traineeService = new TraineeServiceImpl(traineeDAO,userDAO);
    }

    @Test
    void testGetUserFromTrainee() {
        // Arrange
        Trainee trainee = createTestTrainee(1L);
        User user = createTestUser(1L, "Jane", "Smith", "jane.smith");

        when(userDAO.read(1L)).thenReturn(Optional.of(user));

        // Act
        User result = traineeService.getUser(trainee);

        // Assert
        assertNotNull(result, "User should not be null");
        assertEquals(user, result, "Returned user should match the mock user");
        verify(userDAO).read(1L);
    }

    @Test
    void testGetUserThrowsExceptionWhenUserNotFound() {
        // Arrange
        Trainee trainee = createTestTrainee(1L);

        when(userDAO.read(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            traineeService.getUser(trainee);
        });
        assertEquals("User not found for ID: 1", exception.getMessage());
        verify(userDAO).read(1L);
    }

    @Test
    void testPrepareUserWithMissingUsername() {
        Trainee trainee = createTestTrainee(1L);
        trainee.setUsername(null); // Simulate missing username

        User existingUser = createTestUser(2L, "Jane", "Smith", "jane.smith");
        when(userDAO.getAll()).thenReturn(Arrays.asList(existingUser));

        // Act
        traineeService.prepareUser(trainee);

        // Assert
        assertNotNull(trainee.getUsername(), "Username should be generated");
        assertTrue(trainee.getUsername().startsWith("john.doe"), "Username should follow the format 'first.last'");
        verify(userDAO).getAll();

    }

    @Test
    void testPrepareUserWithMissingPassword() {
        // Arrange
        Trainee trainee = createTestTrainee(1L);
        trainee.setPassword(null); // Simulate missing password

        // Act
        traineeService.prepareUser(trainee);

        // Assert
        assertNotNull(trainee.getPassword(), "Password should be generated");
    }

      @Test
    void testGetAllTrainees() {
        Trainee trainee1 = createTestTrainee(1L);
        Trainee trainee2 = createTestTrainee(2L);
        List<Trainee> trainees = Arrays.asList(trainee1, trainee2);

        when(traineeDAO.getAll()).thenReturn(trainees);

        List<Trainee> result = traineeService.getAll();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(trainees));
        verify(traineeDAO).getAll();
    }

    private Trainee createTestTrainee(Long id) {
        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setFirstName("john");
        trainee.setLastName("doe");
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        trainee.setAddress("123 Test St");
        return trainee;
    }

    private User createTestUser(Long id, String firstName, String lastName, String username) {
        User user = new Trainee(); // Используем Trainee, так как User абстрактный
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword("password");
        user.setActive(true);
        return user;
    }

}