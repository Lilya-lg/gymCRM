package uz.gym.training.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.gym.training.domain.TrainingSession;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainingRepositoryTest {

  private TrainingRepository trainingRepository;

  @BeforeEach
  void setUp() {
    trainingRepository = new TrainingRepository();
  }

  @Test
  void testAddTraining_NewTrainer() {
    String trainerUsername = "trainer1";
    TrainingSession session = new TrainingSession();
    session.setTrainingDate(LocalDate.of(2024, 3, 15));
    session.setDuration(60);

    trainingRepository.addTraining(trainerUsername, session);

    List<TrainingSession> sessions = trainingRepository.getTrainingsByTrainer(trainerUsername);
    assertFalse(sessions.isEmpty(), "Trainer should have training sessions.");
    assertEquals(1, sessions.size(), "Trainer should have exactly one session.");
    assertEquals(session, sessions.get(0), "The stored session should match the added session.");
  }

  @Test
  void testGetTrainingsByTrainer_NoSessions() {
    String trainerUsername = "unknownTrainer";
    List<TrainingSession> sessions = trainingRepository.getTrainingsByTrainer(trainerUsername);
    assertTrue(sessions.isEmpty(), "Non-existent trainer should return an empty session list.");
  }

  @Test
  void testRemoveTraining_Success() {
    String trainerUsername = "trainer2";
    TrainingSession session = new TrainingSession();
    session.setTrainingDate(LocalDate.of(2024, 3, 20));
    session.setDuration(90);

    trainingRepository.addTraining(trainerUsername, session);
    assertFalse(
        trainingRepository.getTrainingsByTrainer(trainerUsername).isEmpty(),
        "Session should be added.");

    trainingRepository.removeTraining(trainerUsername, session);
    List<TrainingSession> sessions = trainingRepository.getTrainingsByTrainer(trainerUsername);
    assertTrue(sessions.isEmpty(), "Trainer's session list should be empty after removal.");
  }

  @Test
  void testRemoveTraining_NonExistingSession() {
    String trainerUsername = "trainer3";
    TrainingSession session = new TrainingSession();
    session.setTrainingDate(LocalDate.of(2024, 4, 10));
    session.setDuration(120);

    trainingRepository.removeTraining(trainerUsername, session);

    List<TrainingSession> sessions = trainingRepository.getTrainingsByTrainer(trainerUsername);
    assertTrue(
        sessions.isEmpty(), "Removing a non-existent session should not affect the repository.");
  }

  @Test
  void testTrainerExists() {
    String trainerUsername = "trainer4";
    TrainingSession session = new TrainingSession();
    session.setTrainingDate(LocalDate.of(2024, 5, 5));
    session.setDuration(75);

    assertFalse(
        trainingRepository.trainerExists(trainerUsername), "Trainer should not exist initially.");

    trainingRepository.addTraining(trainerUsername, session);

    assertTrue(
        trainingRepository.trainerExists(trainerUsername),
        "Trainer should exist after adding a session.");
  }
}
