package uz.micro.gym.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.micro.gym.domain.Trainee;
import uz.micro.gym.domain.Trainer;
import uz.micro.gym.domain.Training;
import uz.micro.gym.domain.User;
import uz.micro.gym.dto.TraineeProfileDTO;
import uz.micro.gym.dto.TraineeUpdateDTO;
import uz.micro.gym.mapper.Mapper;
import uz.micro.gym.repository.TraineeRepository;
import uz.micro.gym.repository.TrainerRepository;
import uz.micro.gym.repository.TrainingRepository;
import uz.micro.gym.repository.UserRepository;

import java.time.LocalDate;
import java.util.Arrays;
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
    private UserRepository userRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
    void createTrainee_withNullUser_throwsException() {
        Trainee trainee = new Trainee();

        Exception exception = assertThrows(NullPointerException.class, () -> {
            traineeService.create(trainee);
        });

        assertNotNull(exception);
    }

    @Test
    void deleteProfileByUsername() {
        String username = "testUser";
        Trainee trainee = new Trainee();
        trainee.setId(1L);

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(traineeRepository.deleteByUsername(username)).thenReturn(1);

        traineeService.deleteProfileByUsername(username);

        verify(traineeRepository, times(1)).findByUsername(username);
        verify(traineeRepository, times(1)).deleteByUsername(username);
    }


    @Test
    void getTraineeProfile_withNonExistingUser_throwsException() {
        String username = "nonexistent";

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            traineeService.getTraineeProfile(username);
        });

        assertEquals("Trainee not found", exception.getMessage());
        verify(traineeRepository, times(1)).findByUsername(username);
    }


    @Test
    void updateTraineeProfile_withNonExistingUser_throwsException() {
        String username = "unknown";
        TraineeUpdateDTO traineeUpdateDTO = new TraineeUpdateDTO();

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            traineeService.updateTraineeProfile(username, traineeUpdateDTO);
        });

        assertEquals("Trainee with username 'unknown' does not exist", exception.getMessage());
        verify(traineeRepository, times(1)).findByUsername(username);
    }

    @Test
    void updateTraineeTrainerList() {
        String username = "testUser";
        Long trainingId = 1L;
        List<String> trainerIds = Arrays.asList("trainer1", "trainer2");

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        Training training = new Training();
        training.setId(trainingId);
        Trainer trainer1 = new Trainer();
        trainer1.setId(101L);
        Trainer trainer2 = new Trainer();
        trainer2.setId(102L);
        List<Trainer> trainers = Arrays.asList(trainer1, trainer2);

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));
        when(trainerRepository.findByUsernameIn(trainerIds)).thenReturn(trainers);
        when(trainingRepository.findTrainersByTraineeId(trainee.getId())).thenReturn(trainers);

        List<Trainer> result = traineeService.updateTraineeTrainerList(username, trainingId, trainerIds);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(traineeRepository, times(1)).findByUsername(username);
        verify(trainingRepository, times(1)).findById(trainingId);
        verify(trainerRepository, times(1)).findByUsernameIn(trainerIds);
    }

    @Test
    void updateTraineeTrainerList_withInvalidTrainee_throwsException() {
        String username = "invalidUser";
        Long trainingId = 1L;
        List<String> trainerIds = Arrays.asList("trainer1", "trainer2");

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            traineeService.updateTraineeTrainerList(username, trainingId, trainerIds);
        });

        assertEquals("Trainee not found", exception.getMessage());
        verify(traineeRepository, times(1)).findByUsername(username);
    }

    @Test
    void getTraineeProfile_withValidUser_returnsProfile() {
        String username = "validUser";
        Trainee trainee = new Trainee();
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setIsActive(true);
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(trainingRepository.findTrainersByTraineeId(trainee.getId())).thenReturn(Collections.emptyList());

        TraineeProfileDTO profileDTO = traineeService.getTraineeProfile(username);

        assertNotNull(profileDTO);
        assertEquals("John", profileDTO.getFirstName());
        assertEquals("Doe", profileDTO.getSecondName());
        assertEquals("1990-01-01", profileDTO.getDateOfBirth());
        assertEquals("123 Main St", profileDTO.getAddress());
        assertTrue(profileDTO.isActive());
        assertTrue(profileDTO.getTrainers().isEmpty());

        verify(traineeRepository, times(1)).findByUsername(username);
        verify(trainingRepository, times(1)).findTrainersByTraineeId(trainee.getId());
    }

    @Test
    void updateTraineeProfile_withValidUserAndActiveStatusChange_updatesProfile() {
        String username = "validUser";
        Trainee trainee = new Trainee();
        User user = new User();
        user.setUsername(username);
        user.setIsActive(false);
        trainee.setUser(user);

        TraineeUpdateDTO traineeUpdateDTO = new TraineeUpdateDTO();
        traineeUpdateDTO.setIsActive("true");

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        doNothing().when(mapper).updateTraineeFromDTO(traineeUpdateDTO, trainee);


        traineeService.updateTraineeProfile(username, traineeUpdateDTO);

        assertTrue(trainee.getUser().getIsActive());


        verify(traineeRepository, times(2)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(mapper, times(1)).updateTraineeFromDTO(traineeUpdateDTO, trainee);
        verify(traineeRepository, times(1)).save(trainee);
    }
}
