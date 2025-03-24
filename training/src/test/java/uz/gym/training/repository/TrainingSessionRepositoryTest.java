package uz.gym.training.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.gym.training.domain.Trainer;
import uz.gym.training.domain.TrainingSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrainingSessionRepositoryTest {

    private TrainingSessionRepository trainingSessionRepository;

    @BeforeEach
    void setUp() {
        trainingSessionRepository = new TrainingSessionRepository();
    }

    @Test
    void testSaveAndFindById() {
        TrainingSession session = new TrainingSession();
        session.setId(1L);
        session.setDuration(60);

        trainingSessionRepository.save(session);

        Optional<TrainingSession> foundSession = trainingSessionRepository.findById(1L);
        assertTrue(foundSession.isPresent());
        assertEquals(60, foundSession.get().getDuration());
    }

    @Test
    void testFindAll() {
        TrainingSession session1 = new TrainingSession();
        session1.setId(2L);
        session1.setDuration(45);

        TrainingSession session2 = new TrainingSession();
        session2.setId(3L);
        session2.setDuration(90);

        trainingSessionRepository.save(session1);
        trainingSessionRepository.save(session2);

        List<TrainingSession> allSessions = trainingSessionRepository.findAll();
        assertEquals(2, allSessions.size());
    }

    @Test
    void testFindByTrainer() {
        Trainer trainer = new Trainer();
        trainer.setId(10L);
        trainer.setUsername("TrainerA");

        TrainingSession session1 = new TrainingSession();
        session1.setId(4L);
        session1.setTrainer(trainer);
        session1.setDuration(30);
        session1.setTrainingDate(LocalDate.of(2024, 3, 10));

        TrainingSession session2 = new TrainingSession();
        session2.setId(5L);
        session2.setTrainer(trainer);
        session2.setDuration(90);
        session2.setTrainingDate(LocalDate.of(2024, 4, 5));

        trainingSessionRepository.save(session1);
        trainingSessionRepository.save(session2);

        List<TrainingSession> trainerSessions = trainingSessionRepository.findByTrainer(trainer);
        assertEquals(2, trainerSessions.size());
        assertTrue(trainerSessions.stream().anyMatch(s -> s.getDuration() == 30));
        assertTrue(trainerSessions.stream().anyMatch(s -> s.getDuration() == 90));
    }

    @Test
    void testDeleteById() {
        TrainingSession session = new TrainingSession();
        session.setId(6L);
        session.setDuration(60);

        trainingSessionRepository.save(session);
        assertTrue(trainingSessionRepository.findById(6L).isPresent());

        trainingSessionRepository.deleteById(6L);
        assertFalse(trainingSessionRepository.findById(6L).isPresent());
    }
}
