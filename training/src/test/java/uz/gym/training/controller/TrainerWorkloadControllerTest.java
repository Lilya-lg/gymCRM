package uz.gym.training.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import uz.gym.training.dto.TrainerSummaryDTO;

import uz.gym.training.security.JwtUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import uz.gym.training.service.TrainerSummaryService;

@WebMvcTest(controllers = TrainerWorkloadController.class)
@AutoConfigureMockMvc(addFilters = true)
class TrainerWorkloadControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private JwtUtil jwtUtil;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private TrainerSummaryService trainerSummaryService;

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
    when(trainerSummaryService.getTrainerSummary("JohnDoe")).thenReturn(mockSummary);
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

    verify(trainerSummaryService, times(1)).getTrainerSummary("JohnDoe");
  }

  @Test
  @WithMockUser
  void testGetTrainerSummary_NotFound() throws Exception {
    when(trainerSummaryService.getTrainerSummary("UnknownUser")).thenReturn(null);

    mockMvc.perform(get("/api/trainings/UnknownUser/summary")).andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser
  void testGetTrainerSummary_InternalServerError() throws Exception {
    when(trainerSummaryService.getTrainerSummary("JohnDoe"))
        .thenThrow(new RuntimeException("Unexpected error"));

    mockMvc
        .perform(get("/api/trainings/JohnDoe/summary"))
        .andExpect(status().isInternalServerError());
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
