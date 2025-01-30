package uz.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;
import uz.gym.crm.dto.*;
import uz.gym.crm.mapper.Mapper;
import uz.gym.crm.service.abstr.TraineeService;
import uz.gym.crm.util.GlobalExceptionHandler;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ContextConfiguration(classes = TraineeControllerTest.Config.class)
class TraineeControllerTest {

    @Configuration
    static class Config {
        @Bean
        public TraineeController traineeController() {
            return new TraineeController(traineeService(), mapper());
        }

        @Bean
        public TraineeService traineeService() {
            return Mockito.mock(TraineeService.class);
        }

        @Bean
        public Mapper mapper() {
            return Mockito.mock(Mapper.class);
        }

        @Bean
        public GlobalExceptionHandler globalExceptionHandler() {
            return new GlobalExceptionHandler();
        }

        @Bean
        public static PropertySourcesPlaceholderConfigurer propertyConfig() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }

    @Autowired
    private TraineeController traineeController;

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private Mapper mapper;

    private MockMvc mockMvc;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        traineeService = Mockito.mock(TraineeService.class);
        mapper = Mockito.mock(Mapper.class);

        traineeController = new TraineeController(traineeService, mapper);

        mockMvc = MockMvcBuilders
                .standaloneSetup(traineeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getTraineeProfile_ShouldReturnProfile() throws Exception {
        TraineeProfileDTO profileDTO = new TraineeProfileDTO();
        profileDTO.setFirstName("testuser");

        when(traineeService.getTraineeProfile("testuser")).thenReturn(profileDTO);

        mockMvc.perform(get("/api/trainees/profiles/testuser")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("testuser"));
    }

    @Test
    void createTrainee_ShouldReturnCreatedUser() throws Exception {
        TraineeProfileDTO profileDTO = new TraineeProfileDTO();
        profileDTO.setFirstName("newuser");
        profileDTO.setSecondName("new");

        User user = new User();
        user.setUsername("newuser");
        user.setPassword("password");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(mapper.toTrainee(any(TraineeProfileDTO.class))).thenReturn(trainee);
        doNothing().when(traineeService).create(any(Trainee.class));

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void deleteTraineeProfile_ShouldReturnSuccessMessage() throws Exception {
        doNothing().when(traineeService).deleteProfileByUsername("testuser");

        mockMvc.perform(delete("/api/trainees/testuser")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainee profile deleted successfully"));
    }

    @Test
    void deleteTraineeProfile_ShouldReturnBadRequest_WhenTraineeNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Trainee not found")).when(traineeService).deleteProfileByUsername("invalidUser");

        mockMvc.perform(delete("/api/trainees/invalidUser")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Trainee not found"));
    }



    @Test
    void updateTraineeTrainers_ShouldReturnUpdatedTrainers() throws Exception {
        UpdateTraineeTrainersDTO updateDTO = new UpdateTraineeTrainersDTO();
        updateDTO.setTrainerUsernames(List.of("trainer1", "trainer2"));

        Trainer trainer1 = new Trainer();
        Trainer trainer2 = new Trainer();

        TrainerDTO trainerDTO1 = new TrainerDTO();
        TrainerDTO trainerDTO2 = new TrainerDTO();

        when(traineeService.updateTraineeTrainerList("testuser", updateDTO.getTrainerUsernames()))
                .thenReturn(List.of(trainer1, trainer2));
        when(mapper.mapTrainersToProfileDTOs(List.of(trainer1, trainer2)))
                .thenReturn(List.of(trainerDTO1, trainerDTO2));

        mockMvc.perform(put("/api/trainees/update-trainers?traineeUsername=testuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
