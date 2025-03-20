package uz.gym.training.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.gym.training.domain.Trainer;
import uz.gym.training.domain.TrainingSession;
import uz.gym.training.dto.TrainerSummaryDTO;
import uz.gym.training.repository.TrainerRepository;
import uz.gym.training.repository.TrainingSessionRepository;
import uz.gym.training.service.TrainingService;

import java.time.LocalDate;
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

        TrainingSession session = new TrainingSession();
        session.setTrainer(trainer);
        session.setDuration(60);
        session.setTrainingDate(LocalDate.of(2024, 3, 15)); // Set valid training date
        List<TrainingSession> sessions = Collections.singletonList(session);

        when(trainerRepository.findByUsername("John Doe")).thenReturn(Optional.of(trainer));
        when(trainingSessionRepository.findByTrainer(trainer)).thenReturn(sessions);

        // Act
        TrainerSummaryDTO result = trainingService.getMonthlySummary("John Doe");

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getUsername());
        assertEquals(1, result.getMonthlySummaries().size());
        verify(trainerRepository, times(1)).findByUsername("John Doe");
        verify(trainingSessionRepository, times(1)).findByTrainer(trainer);
    }
}

