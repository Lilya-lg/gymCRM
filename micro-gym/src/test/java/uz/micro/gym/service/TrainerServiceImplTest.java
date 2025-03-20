package uz.micro.gym.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.micro.gym.domain.Trainer;
import uz.micro.gym.domain.User;
import uz.micro.gym.dto.TrainerProfileDTO;
import uz.micro.gym.mapper.Mapper;
import uz.micro.gym.repository.TrainerRepository;
import uz.micro.gym.repository.TrainingRepository;
import uz.micro.gym.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainer() {
        Trainer trainer = new Trainer();
        User user = new User();
        trainer.setUser(user);

        trainerService.create(trainer);

        verify(userRepository, times(1)).save(user);
        verify(trainerRepository, times(1)).save(trainer);
    }

    @Test
    void createTrainer_withNullUser_throwsException() {
        Trainer trainer = new Trainer();

        Exception exception = assertThrows(NullPointerException.class, () -> {
            trainerService.create(trainer);
        });

        assertNotNull(exception);
    }

    @Test
    void getUnassignedTrainersForTrainee() {
        String traineeUsername = "testUser";

        when(trainerRepository.getUnassignedTrainersByTraineeUsername(traineeUsername))
                .thenReturn(Collections.emptyList());

        List<Trainer> result = trainerService.getUnassignedTrainersForTrainee(traineeUsername);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(trainerRepository, times(1)).getUnassignedTrainersByTraineeUsername(traineeUsername);
    }

    @Test
    void getTrainerProfile() {
        String username = "testUser";
        Trainer trainer = new Trainer();
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        trainer.setUser(user);

        TrainerProfileDTO trainerProfileDTO = new TrainerProfileDTO();
        trainerProfileDTO.setFirstName("John");
        trainerProfileDTO.setSecondName("Doe");

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));
        when(mapper.mapToTrainerProfileDTO(trainer)).thenReturn(trainerProfileDTO);

        TrainerProfileDTO result = trainerService.getTrainerProfile(username);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getSecondName());

        verify(trainerRepository, times(1)).findByUsername(username);
        verify(mapper, times(1)).mapToTrainerProfileDTO(trainer);
    }

    @Test
    void getTrainerProfile_withNonExistingUser_throwsException() {
        String username = "nonexistent";

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            trainerService.getTrainerProfile(username);
        });

        assertEquals("Trainee not found", exception.getMessage());
        verify(trainerRepository, times(1)).findByUsername(username);
    }





    @Test
    void findByUsername() {
        String username = "trainer1";
        Trainer trainer = new Trainer();

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = trainerService.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());
        verify(trainerRepository, times(1)).findByUsername(username);
    }

    @Test
    void findByUsername_withNonExistingUser_returnsEmptyOptional() {
        String username = "unknown";

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<Trainer> result = trainerService.findByUsername(username);

        assertFalse(result.isPresent());
        verify(trainerRepository, times(1)).findByUsername(username);
    }

}
