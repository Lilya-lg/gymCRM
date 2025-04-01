package uz.gym.training.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uz.gym.training.domain.TrainingSession;
import uz.gym.training.dto.MonthSummaryDTO;
import uz.gym.training.dto.TrainerSummaryDTO;
import uz.gym.training.dto.TrainingSessionDTO;
import uz.gym.training.repository.TrainingRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

  @Mock private TrainingRepository trainingRepository;

  @InjectMocks private TrainingService trainingService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetTrainerSummary_TrainerWithSessions() {
    String trainerUsername = "JohnDoe";

    TrainingSession session1 = new TrainingSession();
    session1.setTrainingDate(LocalDate.of(2024, 3, 15));
    session1.setDuration(60);

    TrainingSession session2 = new TrainingSession();
    session2.setTrainingDate(LocalDate.of(2024, 3, 20));
    session2.setDuration(90);

    TrainingSession session3 = new TrainingSession();
    session3.setTrainingDate(LocalDate.of(2024, 4, 5));
    session3.setDuration(120);

    List<TrainingSession> sessions = List.of(session1, session2, session3);

    when(trainingRepository.getTrainingsByTrainer(trainerUsername)).thenReturn(sessions);

    TrainerSummaryDTO result = trainingService.getMonthlySummary(trainerUsername);

    assertNotNull(result);
    assertEquals(trainerUsername, result.getUsername());

    assertTrue(result.getMonthlySummaries().containsKey(2024), "Year 2024 should be present");
    List<MonthSummaryDTO> summaries = result.getMonthlySummaries().get(2024);
    assertNotNull(summaries);

    assertTrue(summaries.stream().anyMatch(s -> s.getMonth().equals(Month.MARCH)), "MARCH missing");
    assertTrue(summaries.stream().anyMatch(s -> s.getMonth().equals(Month.APRIL)), "APRIL missing");

    assertEquals(
        150,
        summaries.stream()
            .filter(s -> s.getMonth().equals(Month.MARCH))
            .mapToInt(MonthSummaryDTO::getTotalTrainingDuration)
            .sum(),
        "Total training duration for MARCH is incorrect");

    assertEquals(
        120,
        summaries.stream()
            .filter(s -> s.getMonth().equals(Month.APRIL))
            .mapToInt(MonthSummaryDTO::getTotalTrainingDuration)
            .sum(),
        "Total training duration for APRIL is incorrect");

    verify(trainingRepository, times(1)).getTrainingsByTrainer(trainerUsername);
  }

  @Test
  void testGetTrainerSummary_NoSessions() {
    String trainerUsername = "NoSessionsTrainer";

    when(trainingRepository.getTrainingsByTrainer(trainerUsername))
        .thenReturn(Collections.emptyList());

    TrainerSummaryDTO result = trainingService.getMonthlySummary(trainerUsername);

    assertNotNull(result);
    assertEquals(trainerUsername, result.getUsername());
    assertTrue(
        result.getMonthlySummaries().isEmpty(),
        "Summary should be empty for a trainer with no sessions");
  }

  @Test
  void testAddTraining_NewTrainer() {
    TrainingSessionDTO sessionDTO = new TrainingSessionDTO();
    sessionDTO.setUsername("newTrainer");
    sessionDTO.setTrainingDate(LocalDate.of(2024, 5, 10));
    sessionDTO.setDuration(75);

    when(trainingRepository.getTrainingsByTrainer("newTrainer"))
        .thenReturn(Collections.emptyList());

    trainingService.addTraining(sessionDTO);

    verify(trainingRepository,
            times(1)).addTraining(eq("newTrainer"),
            any(TrainingSession.class));
  }

  @Test
  void testAddTraining_ExistingTrainer() {
    String trainerUsername = "existingTrainer";

    TrainingSessionDTO sessionDTO = new TrainingSessionDTO();
    sessionDTO.setUsername(trainerUsername);
    sessionDTO.setTrainingDate(LocalDate.of(2024, 5, 10));
    sessionDTO.setDuration(60);

    when(trainingRepository.getTrainingsByTrainer(trainerUsername))
        .thenReturn(List.of(new TrainingSession()));

    trainingService.addTraining(sessionDTO);

    verify(trainingRepository, times(1))
        .addTraining(eq(trainerUsername), any(TrainingSession.class));
  }

  @Test
  void testDeleteTraining_Success() {
    String trainerUsername = "JohnDoe";

    TrainingSessionDTO sessionDTO = new TrainingSessionDTO();
    sessionDTO.setUsername(trainerUsername);
    sessionDTO.setTrainingDate(LocalDate.of(2024, 5, 10));
    sessionDTO.setDuration(60);

    TrainingSession session = new TrainingSession();
    session.setId(1L);
    session.setTrainingDate(LocalDate.of(2024, 5, 10));
    session.setDuration(60);

    when(trainingRepository.getTrainingsByTrainer(trainerUsername)).thenReturn(List.of(session));

    trainingService.deleteTraining(sessionDTO);

    verify(trainingRepository, times(1))
            .removeTraining(eq(trainerUsername), eq(session));
  }

  @Test
  void testDeleteTraining_TrainerNotFound() {
    String trainerUsername = "UnknownTrainer";
    TrainingSessionDTO sessionDTO = new TrainingSessionDTO();
    sessionDTO.setUsername(trainerUsername);

    when(trainingRepository.getTrainingsByTrainer(trainerUsername))
        .thenReturn(Collections.emptyList());

    assertThrows(EntityNotFoundException.class, () ->
            trainingService.deleteTraining(sessionDTO));
  }

  @Test
  void testDeleteTraining_NoSessions() {
    String trainerUsername = "JohnDoe";
    TrainingSessionDTO sessionDTO = new TrainingSessionDTO();
    sessionDTO.setUsername(trainerUsername);

    when(trainingRepository.getTrainingsByTrainer(trainerUsername))
        .thenReturn(Collections.emptyList());

    assertThrows(EntityNotFoundException.class, () ->
            trainingService.deleteTraining(sessionDTO));
  }
}
