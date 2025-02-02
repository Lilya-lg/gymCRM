package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;
import uz.gym.crm.dto.TrainerProfileDTO;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.repository.TrainerRepository;
import uz.gym.crm.repository.TrainingRepository;
import uz.gym.crm.repository.UserRepository;

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
    void getUnassignedTrainersForTrainee() {
        String traineeUsername = "testUser";

        when(trainerRepository.getUnassignedTrainersByTraineeUsername(traineeUsername)).thenReturn(Collections.emptyList());

        List<Trainer> result = trainerService.getUnassignedTrainersForTrainee(traineeUsername);

        assertNotNull(result);
        assertTrue(result.isEmpty());
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

}
