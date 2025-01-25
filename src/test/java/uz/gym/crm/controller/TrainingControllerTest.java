package uz.gym.crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uz.gym.crm.domain.Training;
import uz.gym.crm.dto.*;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.TrainingService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private TrainingController trainingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTrainingsForTrainee_ShouldReturnTrainings() {

        TrainingTraineeListDTO traineeListDTO = new TrainingTraineeListDTO();
        traineeListDTO.setUsername("trainee1");

        Training training = new Training();
        TrainingTraineeTrainerDTO dto = new TrainingTraineeTrainerDTO();
        dto.setTrainingName("Sample Training");

        when(trainingService.findByCriteria(anyString(), any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(training));
        when(mapper.mapTrainingsToTrainingDTOs(anyList()))
                .thenReturn(Collections.singletonList(dto));


        ResponseEntity<List<TrainingTraineeTrainerDTO>> response = trainingController.getTrainingsForTrainee(
                traineeListDTO, mockBindingResult(false)
        );


        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Sample Training", response.getBody().get(0).getTrainingName());
    }

    @Test
    void getTrainingsForTrainer_ShouldReturnTrainings() {

        TrainingTrainerListDTO trainerListDTO = new TrainingTrainerListDTO();
        trainerListDTO.setUsername("trainer1");

        Training training = new Training();
        TrainingTraineeTrainerDTO dto = new TrainingTraineeTrainerDTO();
        dto.setTrainingName("Trainer's Training");

        when(trainingService.findByCriteriaForTrainer(anyString(), any(), any(), any()))
                .thenReturn(Collections.singletonList(training));
        when(mapper.mapTrainingsToTrainingDTOs(anyList()))
                .thenReturn(Collections.singletonList(dto));


        ResponseEntity<List<TrainingTraineeTrainerDTO>> response = trainingController.getTrainingsForTrainer(
                trainerListDTO, mockBindingResult(false)
        );


        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Trainer's Training", response.getBody().get(0).getTrainingName());
    }

    @Test
    void createTraining_ShouldReturnSuccessMessage() {
        // Arrange
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTraineeUsername("trainee1");
        trainingDTO.setTrainerUsername("trainer1");

        Training training = new Training();
        when(mapper.toTraining(trainingDTO)).thenReturn(training);
        doNothing().when(trainingService).linkTraineeTrainer(training, "trainee1", "trainer1");
        doNothing().when(trainingService).create(training);


        ResponseEntity<String> response = trainingController.createTraining(trainingDTO, mockBindingResult(false));


        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Training was created successfully", response.getBody());
    }


    private BindingResult mockBindingResult(boolean hasErrors) {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(hasErrors);
        return bindingResult;
    }

    @Test
    void getTrainingsForTrainee_ShouldReturnBadRequest_WhenValidationFails() {
        // Arrange
        BindingResult bindingResult = mockBindingResult(true); // Simulate validation errors
        TrainingTraineeListDTO traineeListDTO = new TrainingTraineeListDTO();

        // Act
        ResponseEntity<List<TrainingTraineeTrainerDTO>> response = trainingController.getTrainingsForTrainee(traineeListDTO, bindingResult);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getTrainingsForTrainee_ShouldHandleIllegalArgumentException() {

        TrainingTraineeListDTO traineeListDTO = new TrainingTraineeListDTO();
        traineeListDTO.setUsername("trainee1");

        when(trainingService.findByCriteria(anyString(), any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid request"));

        ResponseEntity<List<TrainingTraineeTrainerDTO>> response = trainingController.getTrainingsForTrainee(traineeListDTO, mockBindingResult(false));


        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getTrainingsForTrainee_ShouldHandleUnexpectedException() {

        TrainingTraineeListDTO traineeListDTO = new TrainingTraineeListDTO();
        traineeListDTO.setUsername("trainee1");

        when(trainingService.findByCriteria(anyString(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Unexpected error"));


        ResponseEntity<List<TrainingTraineeTrainerDTO>> response = trainingController.getTrainingsForTrainee(traineeListDTO, mockBindingResult(false));


        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void createTraining_ShouldReturnBadRequest_WhenValidationFails() {

        BindingResult bindingResult = mockBindingResult(true); // Simulate validation errors
        TrainingDTO trainingDTO = new TrainingDTO();


        ResponseEntity<String> response = trainingController.createTraining(trainingDTO, bindingResult);

       
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody());
    }

    @Test
    void createTraining_ShouldHandleIllegalArgumentException() {

        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTraineeUsername("trainee1");
        trainingDTO.setTrainerUsername("trainer1");

        Training training = new Training();
        when(mapper.toTraining(trainingDTO)).thenReturn(training);
        doThrow(new IllegalArgumentException("Invalid input")).when(trainingService).linkTraineeTrainer(any(Training.class), anyString(), anyString());


        ResponseEntity<String> response = trainingController.createTraining(trainingDTO, mockBindingResult(false));


        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void createTraining_ShouldHandleUnexpectedException() {

        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTraineeUsername("trainee1");
        trainingDTO.setTrainerUsername("trainer1");

        Training training = new Training();
        when(mapper.toTraining(trainingDTO)).thenReturn(training);
        doThrow(new RuntimeException("Unexpected error")).when(trainingService).linkTraineeTrainer(any(Training.class), anyString(), anyString());


        ResponseEntity<String> response = trainingController.createTraining(trainingDTO, mockBindingResult(false));


        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getTrainingsForTrainer_ShouldReturnBadRequest_WhenValidationFails() {

        BindingResult bindingResult = mockBindingResult(true); // Simulate validation errors
        TrainingTrainerListDTO trainerListDTO = new TrainingTrainerListDTO();


        ResponseEntity<List<TrainingTraineeTrainerDTO>> response = trainingController.getTrainingsForTrainer(trainerListDTO, bindingResult);


        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getTrainingsForTrainer_ShouldHandleIllegalArgumentException() {

        TrainingTrainerListDTO trainerListDTO = new TrainingTrainerListDTO();
        trainerListDTO.setUsername("trainer1");

        when(trainingService.findByCriteriaForTrainer(anyString(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid request"));


        ResponseEntity<List<TrainingTraineeTrainerDTO>> response = trainingController.getTrainingsForTrainer(trainerListDTO, mockBindingResult(false));

        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getTrainingsForTrainer_ShouldHandleUnexpectedException() {

        TrainingTrainerListDTO trainerListDTO = new TrainingTrainerListDTO();
        trainerListDTO.setUsername("trainer1");

        when(trainingService.findByCriteriaForTrainer(anyString(), any(), any(), any()))
                .thenThrow(new RuntimeException("Unexpected error"));


        ResponseEntity<List<TrainingTraineeTrainerDTO>> response = trainingController.getTrainingsForTrainer(trainerListDTO, mockBindingResult(false));


        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
