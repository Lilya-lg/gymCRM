package uz.gym.training.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import uz.gym.training.dto.TrainerSummaryDTO;
import uz.gym.crm.dto.TrainingSessionDTO;
import uz.gym.training.service.TrainingService;
import uz.gym.training.security.JwtUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = TrainerWorkloadController.class)
@AutoConfigureMockMvc(addFilters = true)
class TrainerWorkloadControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private TrainingService trainingService;

  @MockBean private JwtUtil jwtUtil;

  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    TrainerSummaryDTO mockSummary = new TrainerSummaryDTO();
    mockSummary.setUsername("JohnDoe");
    mockSummary.setFirstName("John");
    mockSummary.setLastName("Doe");
    mockSummary.setStatus("Active");
    Integer[] array = {2025};
    mockSummary.setYears(new ArrayList<Integer>(Arrays.asList(array)));
    mockSummary.setMonthlySummaries(Collections.EMPTY_MAP);

    when(trainingService.getMonthlySummary("JohnDoe")).thenReturn(mockSummary);
  }

  @Test
  @WithMockUser
  void testGetTrainerSummary() throws Exception {
    mockMvc
        .perform(get("/api/trainings/JohnDoe/summary"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("JohnDoe"))
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.lastName").value("Doe"))
        .andExpect(jsonPath("$.status").value("Active"))
        .andExpect(jsonPath("$.years").value(2025))
        .andExpect(jsonPath("$.monthlySummaries").isEmpty());

    verify(trainingService, times(1)).getMonthlySummary("JohnDoe");
  }

  @Test
  @WithMockUser // Simulate authenticated user
  void testGetTrainerSummary_NotFound() throws Exception {
    when(trainingService.getMonthlySummary("UnknownUser")).thenReturn(null);

    mockMvc.perform(get("/api/trainings/UnknownUser/summary")).andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser
  void testGetTrainerSummary_InternalServerError() throws Exception {
    when(trainingService.getMonthlySummary("JohnDoe"))
        .thenThrow(new RuntimeException("Unexpected error"));

    mockMvc
        .perform(get("/api/trainings/JohnDoe/summary"))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser
  void testProcessTraining() throws Exception {
    TrainingSessionDTO sessionDTO = new TrainingSessionDTO();
    sessionDTO.setUsername("JohnDoe");
    sessionDTO.setTrainingDate(LocalDate.now());
    sessionDTO.setActionType("ADD");
    sessionDTO.setDuration(60);

    mockMvc
        .perform(
            post("/api/trainings")
                .with(csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(sessionDTO)))
        .andExpect(status().isOk());

    ArgumentCaptor<TrainingSessionDTO> captor = ArgumentCaptor.forClass(TrainingSessionDTO.class);
    verify(trainingService, times(1)).addTraining(captor.capture());
    TrainingSessionDTO capturedDTO = captor.getValue();
    assertEquals("JohnDoe", capturedDTO.getUsername());
  }

  @Test
  void testUnauthorizedAccessToGetTrainerSummary() throws Exception {
    mockMvc.perform(get("/api/trainings/JohnDoe/summary")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser
  void testInvalidTokenAccess() throws Exception {
    when(jwtUtil.extractUsername(anyString())).thenThrow(new RuntimeException("Invalid token"));

    mockMvc
        .perform(
            get("/api/trainings/JohnDoe/summary").header("Authorization", "Bearer invalid_token"))
        .andExpect(status().isUnauthorized());
  }
}
