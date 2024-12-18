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
        traineeService = new TraineeServiceImpl(traineeDAO, userDAO);
    }

    @Test
    void testCreateTraineeWithExistingUser() {
        Trainee trainee = createTestTrainee(1L);
        User user = createTestUser(trainee.getUserId());
        when(userDAO.findById(trainee.getUserId())).thenReturn(Optional.of(user));

        traineeService.create(trainee);

        verify(userDAO).findById(trainee.getUserId());
        verify(traineeDAO).create(trainee);
    }

    @Test
    void testCreateTraineeWithNonExistingUser() {
        Trainee trainee = createTestTrainee(1L);
        when(userDAO.findById(trainee.getUserId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            traineeService.create(trainee);
        });

        assertEquals("User not found for ID: " + trainee.getUserId(), exception.getMessage());
        verify(userDAO).findById(trainee.getUserId());
        verify(traineeDAO, never()).create(trainee);
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
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        trainee.setAddress("123 Test St");
        trainee.setUserId(100L);
        return trainee;
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
}