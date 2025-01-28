package uz.gym.crm.controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uz.gym.crm.domain.Training;
import uz.gym.crm.dto.*;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.TrainingService;
import uz.gym.crm.util.GlobalExceptionHandler;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class TrainingControllerTest {

    private MockMvc mockMvc;

    private TrainingService trainingService;

    private Mapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainingService = mock(TrainingService.class);
        mapper = mock(Mapper.class);
        TrainingController trainingController = new TrainingController(trainingService, mapper);

        mockMvc = MockMvcBuilders
                .standaloneSetup(trainingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getTrainingsForTrainee_ShouldReturnTrainings() throws Exception {
        TrainingTraineeListDTO traineeListDTO = new TrainingTraineeListDTO();
        traineeListDTO.setUsername("trainee1");

        TrainingTraineeTrainerDTO dto = new TrainingTraineeTrainerDTO();
        dto.setTrainingName("Sample Training");

        when(trainingService.findByCriteria(anyString(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(mapper.mapTrainingsToTrainingDTOs(anyList()))
                .thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/trainings/trainee")
                        .param("username", "trainee1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].trainingName").value("Sample Training"));
    }

    @Test
    void getTrainingsForTrainee_ShouldHandleIllegalArgumentException() throws Exception {
        when(trainingService.findByCriteria(anyString(), any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid request"));


        mockMvc.perform(get("/api/trainings/trainee")
                        .param("username", "trainee1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request"));
    }

    @Test
    void getTrainingsForTrainee_ShouldHandleUnexpectedException() throws Exception {
        when(trainingService.findByCriteria(anyString(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/trainings/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"trainee1\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An unexpected error occurred. Please contact support if the problem persists."));
    }

    @Test
    void createTraining_ShouldReturnSuccessMessage() throws Exception {
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTraineeUsername("trainee1");
        trainingDTO.setTrainerUsername("trainer1");

        Training training = new Training();
        when(mapper.toTraining(any(TrainingDTO.class))).thenReturn(training);
        doNothing().when(trainingService).linkTraineeTrainer(training, "trainee1", "trainer1");
        doNothing().when(trainingService).create(training);

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"traineeUsername\":\"trainee1\",\"trainerUsername\":\"trainer1\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Training was created successfully"));
    }

    @Test
    void createTraining_ShouldHandleIllegalArgumentException() throws Exception {
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTraineeUsername("trainee1");
        trainingDTO.setTrainerUsername("trainer1");

        Training training = new Training();
        when(mapper.toTraining(any(TrainingDTO.class))).thenReturn(training);
        doThrow(new IllegalArgumentException("Invalid input")).when(trainingService).linkTraineeTrainer(any(Training.class), anyString(), anyString());

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"traineeUsername\":\"trainee1\",\"trainerUsername\":\"trainer1\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid input"));
    }

    @Test
    void createTraining_ShouldHandleUnexpectedException() throws Exception {
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTraineeUsername("trainee1");
        trainingDTO.setTrainerUsername("trainer1");

        Training training = new Training();
        when(mapper.toTraining(any(TrainingDTO.class))).thenReturn(training);
        doThrow(new RuntimeException("Unexpected error")).when(trainingService).linkTraineeTrainer(any(Training.class), anyString(), anyString());

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"traineeUsername\":\"trainee1\",\"trainerUsername\":\"trainer1\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An unexpected error occurred. Please contact support if the problem persists."));
    }
}
