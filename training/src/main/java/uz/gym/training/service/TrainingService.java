package uz.gym.training.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import uz.gym.training.domain.TrainingSession;
import uz.gym.training.dto.MonthSummaryDTO;
import uz.gym.training.dto.TrainerSummaryDTO;
import uz.gym.training.dto.TrainingSessionDTO;
import uz.gym.training.repository.TrainingRepository;
import uz.gym.training.service.abstr.BaseService;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrainingService implements BaseService {
  private final TrainingRepository trainingRepository;

  public TrainingService(TrainingRepository trainingRepository) {
    this.trainingRepository = trainingRepository;
  }

  public void addTraining(TrainingSessionDTO sessionDTO) {
    TrainingSession session = new TrainingSession(sessionDTO);
    trainingRepository.addTraining(sessionDTO.getUsername(), session);
  }

  public void deleteTraining(TrainingSessionDTO sessionDTO) {
    String trainerUsername = sessionDTO.getUsername();

    List<TrainingSession> sessions = trainingRepository.getTrainingsByTrainer(trainerUsername);

    if (sessions.isEmpty()) {
      throw new EntityNotFoundException("No training sessions found for trainer: " + trainerUsername);
    }

    // Find the session that matches the given sessionDTO (e.g., by date, duration, etc.)
    Optional<TrainingSession> sessionToRemove = sessions.stream()
            .filter(session -> session.getTrainingDate().equals(sessionDTO.getTrainingDate()) &&
                    session.getDuration() == sessionDTO.getDuration())
            .findFirst();

    if (sessionToRemove.isPresent()) {
      trainingRepository.removeTraining(trainerUsername, sessionToRemove.get());
    } else {
      throw new EntityNotFoundException("Training session not found for removal.");
    }
  }

  public List<TrainingSession> getTrainingsByTrainer(String trainerUsername) {
    return trainingRepository.getTrainingsByTrainer(trainerUsername);
  }

  public TrainerSummaryDTO getMonthlySummary(String trainerUsername) {
    List<TrainingSession> sessions =
            trainingRepository.getTrainingsByTrainer(trainerUsername);

    if (sessions.isEmpty()) {
      return new TrainerSummaryDTO(
          trainerUsername,
          "Unknown",
          "Unknown",
          "false",
          Collections.emptyList(),
          Collections.emptyMap());
    }

    Map<Integer, Map<Month, Integer>> trainingSummary =
        sessions.stream()
            .filter(session -> session.getTrainingDate() != null)
            .collect(
                Collectors.groupingBy(
                    session -> session.getTrainingDate().getYear(),
                    Collectors.groupingBy(
                        session -> session.getTrainingDate().getMonth(),
                        Collectors.summingInt(TrainingSession::getDuration))));

    List<Integer> years = new ArrayList<>(trainingSummary.keySet());
    Collections.sort(years);

    Map<Integer, List<MonthSummaryDTO>> monthlySummaries = new HashMap<>();
    for (var entry : trainingSummary.entrySet()) {
      Integer year = entry.getKey();
      List<MonthSummaryDTO> monthSummaries =
          entry.getValue().entrySet().stream()
              .map(monthEntry -> new MonthSummaryDTO(monthEntry.getKey(),
                      monthEntry.getValue()))
              .sorted(Comparator.comparing(MonthSummaryDTO::getMonth))
              .collect(Collectors.toList());
      monthlySummaries.put(year, monthSummaries);
    }

    return new TrainerSummaryDTO(
        trainerUsername, "Unknown", "Unknown", "true", years,
            monthlySummaries);
  }
}
