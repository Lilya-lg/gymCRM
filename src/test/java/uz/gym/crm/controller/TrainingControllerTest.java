package uz.gym.crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uz.gym.crm.domain.Training;
import uz.gym.crm.dto.*;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.TrainingService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private TrainingController trainingController;

    private TrainingTraineeListDTO trainingTraineeListDTO;
    private TrainingTrainerListDTO trainingTrainerListDTO;
    private TrainingDTO trainingDTO;
    private TrainingTraineeTrainerDTO trainingTraineeTrainerDTO;

    @BeforeEach
    void setUp() {

        trainingTraineeListDTO = new TrainingTraineeListDTO();
        trainingTraineeListDTO.setUsername("trainee1");
        trainingTraineeListDTO.setPeriodFrom(LocalDate.of(2023, 1, 1));
        trainingTraineeListDTO.setPeriodTo(LocalDate.of(2023, 12, 31));
        trainingTraineeListDTO.setTrainerName("trainer1");
        trainingTraineeListDTO.setTrainingType("Fitness");

        trainingTrainerListDTO = new TrainingTrainerListDTO();
        trainingTrainerListDTO.setUsername("trainer1");
        trainingTrainerListDTO.setPeriodFrom(LocalDate.of(2023, 1, 1));
        trainingTrainerListDTO.setPeriodTo(LocalDate.of(2023, 12, 31));
        trainingTrainerListDTO.setTraineeName("trainee1");

        trainingDTO = new TrainingDTO();
        trainingDTO.setTraineeUsername("trainee1");
        trainingDTO.setTrainerUsername("trainer1");
        trainingDTO.setTrainingDate(LocalDate.of(2023, 10, 1));


        trainingTraineeTrainerDTO = new TrainingTraineeTrainerDTO();
        trainingTraineeTrainerDTO.setTrainerName("trainer1");
        trainingTraineeTrainerDTO.setTrainingType("Fitness");
    }

    @Test
    void testGetTrainingsForTrainee() {

        List<Training> trainings = Arrays.asList(new Training(), new Training());
        when(trainingService.findByCriteria("trainee1", "Fitness", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), "trainer1"))
                .thenReturn(trainings);
        when(mapper.mapTrainingsToTrainingDTOs(trainings)).thenReturn(Arrays.asList(trainingTraineeTrainerDTO, trainingTraineeTrainerDTO));


        ResponseEntity<List<TrainingTraineeTrainerDTO>> response = trainingController.getTrainingsForTrainee(trainingTraineeListDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(trainingService, times(1)).findByCriteria("trainee1", "Fitness", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), "trainer1");
        verify(mapper, times(1)).mapTrainingsToTrainingDTOs(trainings);
    }

    @Test
    void testGetTrainingsForTrainer() {

        List<Training> trainings = Arrays.asList(new Training(), new Training());
        when(trainingService.findByCriteriaForTrainer("trainer1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), "trainee1"))
                .thenReturn(trainings);
        when(mapper.mapTrainingsToTrainingDTOs(trainings)).thenReturn(Arrays.asList(trainingTraineeTrainerDTO, trainingTraineeTrainerDTO));


        ResponseEntity<List<TrainingTraineeTrainerDTO>> response = trainingController.getTrainingsForTrainer(trainingTrainerListDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(trainingService, times(1)).findByCriteriaForTrainer("trainer1", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), "trainee1");
        verify(mapper, times(1)).mapTrainingsToTrainingDTOs(trainings);
    }

    @Test
    void testCreateTraining() {

        Training training = new Training();
        when(mapper.toTraining(trainingDTO)).thenReturn(training);
        doNothing().when(trainingService).linkTraineeTrainer(training, "trainee1", "trainer1");
        doNothing().when(trainingService).create(training);

        ResponseEntity<String> response = trainingController.createTraining(trainingDTO);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Training was created successfully", response.getBody());
        verify(trainingService, times(1)).linkTraineeTrainer(training, "trainee1", "trainer1");
        verify(trainingService, times(1)).create(training);
    }
}