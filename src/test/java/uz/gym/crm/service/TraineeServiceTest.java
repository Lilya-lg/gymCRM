package uz.gym.crm.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.gym.crm.dao.TraineeDAO;
import uz.gym.crm.dao.TraineeDAOImpl;
import uz.gym.crm.dao.UserDAO;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceTest {
    private TraineeDAO traineeDAO;
    private UserDAO userDAO;
    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        traineeDAO = mock(TraineeDAO.class);
        userDAO = mock(UserDAO.class);
        traineeService = new TraineeServiceImpl(traineeDAO, userDAO);
    }

    @Test
    void testCreateTrainee() {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setFirstName("Jane");
        mockUser.setLastName("Doe");
        mockUser.setUsername("jane.doe");
        mockUser.setPassword("password123");

        when(userDAO.findById(1)).thenReturn(Optional.of(mockUser));

        // Mock traineeDAO.create()
        doNothing().when(traineeDAO).create(any(Trainee.class));

        // Create a trainee
        Trainee trainee = new Trainee();
        trainee.setId(1);
        trainee.setUserId(1);

        traineeService.create(trainee); // Save the trainee

        verify(userDAO).findById(1);
        verify(traineeDAO).create(trainee);

        assertEquals(1, trainee.getUserId(), "Trainee's userId should match the resolved user's ID.");
    }

    @Test
    void testCreateTraineeWithNonExistentUser() {
        when(userDAO.findById(99)).thenReturn(Optional.empty()); // Simulate user not found

        Trainee trainee = new Trainee();
        trainee.setId(1);
        trainee.setUserId(99);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> traineeService.create(trainee));
        assertEquals("User not found for ID: 99", exception.getMessage());

        verify(userDAO).findById(99);
        verifyNoInteractions(traineeDAO);
    }

    @Test
    void testGetAllTrainees() {
        // Mock storage for trainees
        List<Trainee> mockStorage = new ArrayList<>();
        when(traineeDAO.getAll()).thenReturn(mockStorage);

        // Mock behavior for traineeDAO.create()
        doAnswer(invocation -> {
            Trainee trainee = invocation.getArgument(0);
            mockStorage.add(trainee);
            return null;
        }).when(traineeDAO).create(any(Trainee.class));


        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setFirstName("Jane");
        mockUser.setLastName("Doe");
        when(userDAO.findById(1)).thenReturn(Optional.of(mockUser));

        // Create a trainee
        Trainee trainee = new Trainee();
        trainee.setId(1);
        trainee.setUserId(1);

        traineeService.create(trainee);

        // Verify retrieval
        List<Trainee> trainees = traineeService.getAll();
        assertFalse(trainees.isEmpty(), "Trainees list should not be empty.");
        assertEquals(1, trainees.size(), "Trainees list size should be 1.");
        assertEquals(trainee, trainees.get(0), "Retrieved trainee should match the created trainee.");
    }
}
