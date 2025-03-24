package uz.gym.training.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.gym.training.domain.Trainer;
import uz.gym.training.domain.TrainingSession;
import uz.gym.training.dto.MonthSummaryDTO;
import uz.gym.training.dto.TrainerSummaryDTO;
import uz.gym.training.dto.TrainingSessionDTO;
import uz.gym.training.repository.TrainerRepository;
import uz.gym.training.repository.TrainingSessionRepository;


import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingSessionRepository trainingSessionRepository;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTrainerSummary() {
        Trainer trainer = new Trainer();
        trainer.setUsername("John Doe");

        TrainingSession session1 = new TrainingSession();
        session1.setTrainer(trainer);
        session1.setDuration(60);
        session1.setTrainingDate(LocalDate.of(2024, 3, 15));

        TrainingSession session2 = new TrainingSession();
        session2.setTrainer(trainer);
        session2.setDuration(90);
        session2.setTrainingDate(LocalDate.of(2024, 3, 20));

        TrainingSession session3 = new TrainingSession();
        session3.setTrainer(trainer);
        session3.setDuration(120);
        session3.setTrainingDate(LocalDate.of(2024, 4, 5));

        List<TrainingSession> sessions = List.of(session1, session2, session3);

        when(trainerRepository.findByUsername("John Doe")).thenReturn(Optional.of(trainer));
        when(trainingSessionRepository.findByTrainer(trainer)).thenReturn(sessions);


        TrainerSummaryDTO result = trainingService.getMonthlySummary("John Doe");
        assertNotNull(result);
        assertEquals("John Doe", result.getUsername());
        assertTrue(result.getMonthlySummaries().containsKey(2024), "Map should contain key '2024'");
        List<MonthSummaryDTO> summaries = result.getMonthlySummaries().get(2024);
        assertNotNull(summaries, "The summaries list should not be null");
        assertTrue(summaries.stream().anyMatch(s -> s.getMonth().equals(Month.MARCH)), "Summaries should contain MARCH");
        assertTrue(summaries.stream().anyMatch(s -> s.getMonth().equals(Month.APRIL)), "Summaries should contain APRIL");
        assertEquals(150, summaries.stream().filter(s -> s.getMonth().equals(Month.MARCH)).mapToInt(MonthSummaryDTO::getTotalTrainingDuration).sum(), "Total training duration for MARCH is incorrect");
        assertEquals(120, summaries.stream().filter(s -> s.getMonth().equals(Month.APRIL)).mapToInt(MonthSummaryDTO::getTotalTrainingDuration).sum(), "Total training duration for APRIL is incorrect");
        verify(trainerRepository, times(1)).findByUsername("John Doe");
        verify(trainingSessionRepository, times(1)).findByTrainer(trainer);
    }

    @Test
    void testAddTraining_NewTrainer() {
        TrainingSessionDTO sessionDTO = new TrainingSessionDTO();
        sessionDTO.setUsername("newTrainer");
        sessionDTO.setFirstName("John");
        sessionDTO.setLastName("Doe");
        sessionDTO.setTrainingDate(LocalDate.of(2024, 5, 10));
        sessionDTO.setDuration(75);

        when(trainerRepository.findByUsername("newTrainer")).thenReturn(Optional.empty());
        when(trainerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        trainingService.addTraining(sessionDTO);

        verify(trainerRepository, times(1)).save(any(Trainer.class));
        verify(trainingSessionRepository, times(1)).save(any(TrainingSession.class));
    }

    @Test
    void testAddTraining_ExistingTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("existingTrainer");

        TrainingSessionDTO sessionDTO = new TrainingSessionDTO();
        sessionDTO.setUsername("existingTrainer");
        sessionDTO.setTrainingDate(LocalDate.of(2024, 5, 10));
        sessionDTO.setDuration(60);

        when(trainerRepository.findByUsername("existingTrainer")).thenReturn(Optional.of(trainer));

        trainingService.addTraining(sessionDTO);

        verify(trainerRepository, never()).save(any());
        verify(trainingSessionRepository, times(1)).save(any(TrainingSession.class));
    }

    @Test
    void testDeleteTraining_Success() {
        Trainer trainer = new Trainer();
        trainer.setUsername("JohnDoe");

        TrainingSessionDTO sessionDTO = new TrainingSessionDTO();
        sessionDTO.setUsername("JohnDoe");

        TrainingSession session = new TrainingSession();
        session.setTrainer(trainer);
        session.setId(1L);

        when(trainerRepository.findByUsername("JohnDoe")).thenReturn(Optional.of(trainer));
        when(trainingSessionRepository.findAll()).thenReturn(List.of(session));

        trainingService.deleteTraining(sessionDTO);

        verify(trainingSessionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTraining_TrainerNotFound() {
        TrainingSessionDTO sessionDTO = new TrainingSessionDTO();
        sessionDTO.setUsername("UnknownTrainer");

        when(trainerRepository.findByUsername("UnknownTrainer")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainingService.deleteTraining(sessionDTO));
    }

    @Test
    void testDeleteTraining_NoSessions() {
        Trainer trainer = new Trainer();
        trainer.setUsername("JohnDoe");

        TrainingSessionDTO sessionDTO = new TrainingSessionDTO();
        sessionDTO.setUsername("JohnDoe");

        when(trainerRepository.findByUsername("JohnDoe")).thenReturn(Optional.of(trainer));
        when(trainingSessionRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class, () -> trainingService.deleteTraining(sessionDTO));
    }

}

