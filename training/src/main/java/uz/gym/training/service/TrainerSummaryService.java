package uz.gym.training.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.gym.crm.dto.TrainingSessionDTO;
import uz.gym.training.domain.MonthSummary;
import uz.gym.training.domain.TrainerTrainingSummary;
import uz.gym.training.domain.YearSummary;
import uz.gym.training.dto.MonthSummaryDTO;
import uz.gym.training.dto.TrainerSummaryDTO;
import uz.gym.training.repository.abstr.TrainerTrainingSummaryRepository;
import uz.gym.training.service.abstr.BaseService;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainerSummaryService implements BaseService {

  private static final Logger logger = LoggerFactory.getLogger(TrainerSummaryService.class);

  @Autowired private TrainerTrainingSummaryRepository repository;

  public void addTraining(TrainingSessionDTO event) {
    TrainerTrainingSummary trainerSummary =
        repository
            .findByTrainerUsername(event.getUsername())
            .orElseGet(
                () -> {
                  TrainerTrainingSummary newTrainer = new TrainerTrainingSummary();
                  newTrainer.setTrainerUsername(event.getUsername());
                  newTrainer.setTrainerFirstName(event.getFirstName());
                  newTrainer.setTrainerLastName(event.getLastName());
                  newTrainer.setTrainerStatus(event.isActive());
                  return newTrainer;
                });

    LocalDate trainingDate = event.getTrainingDate();
    int year = trainingDate.getYear();
    String month = trainingDate.getMonth().toString();
    YearSummary yearSummary =
        trainerSummary.getYearsList().stream()
            .filter(ys -> ys.getYear() != null && ys.getYear() == year)
            .findFirst()
            .orElseGet(
                () -> {
                  YearSummary ys = new YearSummary(year);
                  trainerSummary.getYearsList().add(ys);
                  return ys;
                });

    MonthSummary monthSummary =
        yearSummary.getMonthsList().stream()
            .filter(ms -> ms.getMonth() != null && ms.getMonth().equalsIgnoreCase(month))
            .findFirst()
            .orElseGet(
                () -> {
                  MonthSummary ms = new MonthSummary();
                  ms.setMonth(month);
                  ms.setTrainingSummaryDuration(0);
                  yearSummary.getMonthsList().add(ms);
                  return ms;
                });

    int updatedDuration = monthSummary.getTrainingSummaryDuration() + event.getDuration();
    monthSummary.setTrainingSummaryDuration(updatedDuration);
    repository.save(trainerSummary);
  }

  public TrainerSummaryDTO getTrainerSummary(String trainerUsername) {
    Optional<TrainerTrainingSummary> trainerOpt = repository.findByTrainerUsername(trainerUsername);
    if (trainerOpt.isPresent()) {
      TrainerTrainingSummary trainer = trainerOpt.get();
      TrainerSummaryDTO dto = new TrainerSummaryDTO();
      dto.setUsername(trainer.getTrainerUsername());
      dto.setFirstName(trainer.getTrainerFirstName());
      dto.setLastName(trainer.getTrainerLastName());
      dto.setStatus(trainer.getTrainerStatus().toString());

      List<Integer> years =
          trainer.getYearsList().stream().map(YearSummary::getYear).collect(Collectors.toList());
      dto.setYears(years);

      Map<Integer, List<MonthSummaryDTO>> monthlySummaries =
          trainer.getYearsList().stream()
              .collect(
                  Collectors.toMap(
                      YearSummary::getYear,
                      yearSummary ->
                          yearSummary.getMonthsList().stream()
                              .map(
                                  ms -> {
                                    MonthSummaryDTO monthDto = new MonthSummaryDTO();
                                    monthDto.setMonth(Month.valueOf(ms.getMonth()));
                                    monthDto.setTotalTrainingDuration(
                                        ms.getTrainingSummaryDuration());
                                    return monthDto;
                                  })
                              .collect(Collectors.toList())));
      dto.setMonthlySummaries(monthlySummaries);

      return dto;
    }
    return null;
  }

  public void deleteTraining(TrainingSessionDTO sessionDTO) {
    String trainerUsername = sessionDTO.getUsername();

    Optional<TrainerTrainingSummary> trainerOpt = repository.findByTrainerUsername(trainerUsername);
    if (!trainerOpt.isPresent()) {
      throw new EntityNotFoundException(
          "No training sessions found for trainer: " + trainerUsername);
    }

    TrainerTrainingSummary trainer = trainerOpt.get();
    LocalDate trainingDate = sessionDTO.getTrainingDate();
    int year = trainingDate.getYear();
    String month = trainingDate.getMonth().toString();

    Optional<YearSummary> yearOpt =
        trainer.getYearsList().stream()
            .filter(ys -> ys.getYear() != null && ys.getYear() == year)
            .findFirst();
    if (!yearOpt.isPresent()) {
      throw new EntityNotFoundException("Training session not found for removal.");
    }
    YearSummary yearSummary = yearOpt.get();

    Optional<MonthSummary> monthOpt =
        yearSummary.getMonthsList().stream()
            .filter(ms -> ms.getMonth() != null && ms.getMonth().equalsIgnoreCase(month))
            .findFirst();
    if (!monthOpt.isPresent()) {
      throw new EntityNotFoundException("Training session not found for removal.");
    }
    MonthSummary monthSummary = monthOpt.get();

    int currentDuration = monthSummary.getTrainingSummaryDuration();
    int sessionDuration = sessionDTO.getDuration();

    if (currentDuration < sessionDuration) {
      throw new EntityNotFoundException(
          "Training session not found for removal: insufficient duration.");
    }

    monthSummary.setTrainingSummaryDuration(currentDuration - sessionDuration);

    if (monthSummary.getTrainingSummaryDuration() == 0.0) {
      yearSummary.getMonthsList().remove(monthSummary);
    }

    if (yearSummary.getMonthsList().isEmpty()) {
      trainer.getYearsList().remove(yearSummary);
    }

    repository.save(trainer);
  }
}
