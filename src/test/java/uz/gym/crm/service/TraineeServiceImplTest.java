package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;
import uz.gym.crm.domain.User;
import uz.gym.crm.dto.TraineeProfileDTO;
import uz.gym.crm.repository.TraineeRepository;
import uz.gym.crm.repository.TrainerRepository;
import uz.gym.crm.repository.TrainingRepository;
import uz.gym.crm.repository.UserRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee trainee;
    private Training training;
    private Trainer trainer1, trainer2;
    private User user1, user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);


        trainee = new Trainee();
        trainee.setId(1L);

        training = new Training();
        training.setId(100L);
        training.setTrainee(trainee);


        user1 = new User();
        user1.setUsername("trainerJohn");

        user2 = new User();
        user2.setUsername("trainerAlice");

        trainer1 = new Trainer();
        trainer1.setId(101L);
        trainer1.setUser(user1);

        trainer2 = new Trainer();
        trainer2.setId(102L);
        trainer2.setUser(user2);
    }

    @Test
    void createTrainee() {
        Trainee trainee = new Trainee();
        User user = new User();
        trainee.setUser(user);

        traineeService.create(trainee);

        verify(userRepository, times(1)).save(user);
        verify(traineeRepository, times(1)).save(trainee);
    }

    @Test
    void deleteProfileByUsername() {
        String username = "testUser";

        traineeService.deleteProfileByUsername(username);

        verify(traineeRepository, times(1)).deleteByUsername(username);
    }

    @Test
    void getTraineeProfile() {
        String username = "testUser";
        Trainee trainee = new Trainee();
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.now());

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(trainingRepository.findTrainersByTraineeId(trainee.getId())).thenReturn(Collections.emptyList());

        TraineeProfileDTO result = traineeService.getTraineeProfile(username);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getSecondName());
    }
}
