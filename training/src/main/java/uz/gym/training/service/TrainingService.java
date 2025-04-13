package uz.gym.training.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import uz.gym.crm.dto.TrainingSessionDTO;
import uz.gym.training.domain.TrainingSession;
import uz.gym.training.dto.MonthSummaryDTO;
import uz.gym.training.dto.TrainerSummaryDTO;
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
      throw new EntityNotFoundException(
          "No training sessions found for trainer: " + trainerUsername);
    }

    Optional<TrainingSession> sessionToRemove =
        sessions.stream()
            .filter(
                session ->
                    session.getTrainingDate().equals(sessionDTO.getTrainingDate())
                        && session.getDuration() == sessionDTO.getDuration())
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
    List<TrainingSession> sessions = trainingRepository.getTrainingsByTrainer(trainerUsername);

    if (sessions.isEmpty()) {
      throw new EntityNotFoundException(
          "Trainer with username '" + trainerUsername + "' not found or has no sessions.");
    }
    String firstName = sessions.get(0).getFirstName();
    String lastName = sessions.get(0).getLastName();

    Map<Integer, Map<Month, Integer>> trainingSummary = groupSessionsByYearAndMonth(sessions);
    List<Integer> years = extractAndSortYears(trainingSummary);
    Map<Integer, List<MonthSummaryDTO>> monthlySummaries =
        convertToMonthlySummaries(trainingSummary);

    return new TrainerSummaryDTO(
        trainerUsername, firstName, lastName, "true", years, monthlySummaries);
  }

  private Map<Integer, Map<Month, Integer>> groupSessionsByYearAndMonth(
      List<TrainingSession> sessions) {
    return sessions.stream()
        .filter(session -> session.getTrainingDate() != null)
        .collect(
            Collectors.groupingBy(
                session -> session.getTrainingDate().getYear(),
                Collectors.groupingBy(
                    session -> session.getTrainingDate().getMonth(),
                    Collectors.summingInt(TrainingSession::getDuration))));
  }

  private List<Integer> extractAndSortYears(Map<Integer, Map<Month, Integer>> trainingSummary) {
    List<Integer> years = new ArrayList<>(trainingSummary.keySet());
    Collections.sort(years);
    return years;
  }

  private Map<Integer, List<MonthSummaryDTO>> convertToMonthlySummaries(
      Map<Integer, Map<Month, Integer>> trainingSummary) {
    Map<Integer, List<MonthSummaryDTO>> monthlySummaries = new HashMap<>();

    for (Map.Entry<Integer, Map<Month, Integer>> entry : trainingSummary.entrySet()) {
      Integer year = entry.getKey();
      List<MonthSummaryDTO> monthSummaries =
          entry.getValue().entrySet().stream()
              .map(monthEntry -> new MonthSummaryDTO(monthEntry.getKey(), monthEntry.getValue()))
              .sorted(Comparator.comparing(MonthSummaryDTO::getMonth))
              .collect(Collectors.toList());

      monthlySummaries.put(year, monthSummaries);
    }

    return monthlySummaries;
  }
}
