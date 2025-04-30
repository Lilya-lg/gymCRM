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
import uz.gym.training.dto.TrainerSummaryDTO;
import uz.gym.training.mapper.TrainerSummaryConverter;
import uz.gym.training.repository.abstr.TrainerTrainingSummaryRepository;
import uz.gym.training.service.abstr.BaseService;
import uz.gym.training.util.exceptions.InsufficientDurationException;

import java.time.LocalDate;

@Service
public class TrainerSummaryService implements BaseService {

  private static final Logger logger = LoggerFactory.getLogger(TrainerSummaryService.class);

  @Autowired private TrainerTrainingSummaryRepository repository;

  @Override
  public void getOrCreateTraining(TrainingSessionDTO event) {
    validateTrainingDate(event.getTrainingDate());
    TrainerTrainingSummary trainer = getOrCreateTrainerSummary(event);

    int year = event.getTrainingDate().getYear();
    YearSummary yearSummary = getOrCreateYearSummary(trainer, year);

    String month = event.getTrainingDate().getMonth().toString();
    MonthSummary monthSummary = getOrCreateMonthSummary(yearSummary, month);

    int updatedDuration = monthSummary.getTrainingSummaryDuration() + event.getDuration();
    monthSummary.setTrainingSummaryDuration(updatedDuration);

    repository.save(trainer);
  }

  @Override
  public TrainerSummaryDTO getTrainerSummary(String trainerUsername) {
    try{
    TrainerTrainingSummary trainer =
        repository
            .findByTrainerUsername(trainerUsername)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "No training sessions found for trainer: " + trainerUsername));
    return TrainerSummaryConverter.toDto(trainer);
    }
    catch (Exception e){
      e.printStackTrace();
      throw e;
    }
  }

  @Override
  public void deleteTraining(TrainingSessionDTO sessionDTO) {
    TrainerTrainingSummary trainer = findTrainerOrThrow(sessionDTO.getUsername());

    YearSummary yearSummary =
        findYearSummaryOrThrow(
            trainer, sessionDTO.getTrainingDate().getYear(), sessionDTO.getTrainingName());

    MonthSummary monthSummary =
        findMonthSummaryOrThrow(
            yearSummary,
            sessionDTO.getTrainingDate().getMonth().toString(),
            sessionDTO.getTrainingName());

    validateDuration(
        monthSummary.getTrainingSummaryDuration(),
        sessionDTO.getDuration(),
        sessionDTO.getTrainingName());

    adjustSummaries(trainer, yearSummary, monthSummary, sessionDTO.getDuration());

    repository.save(trainer);
  }

  private void validateTrainingDate(LocalDate date) {
    if (date == null) {
      throw new IllegalArgumentException("Training date cannot be null");
    }
    if (date.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Training date cannot be in the future: " + date);
    }
  }

  private TrainerTrainingSummary getOrCreateTrainerSummary(TrainingSessionDTO event) {
    return repository
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
  }

  private YearSummary getOrCreateYearSummary(TrainerTrainingSummary trainer, int year) {
    return trainer.getYearsList().stream()
        .filter(ys -> ys.getYear() != null && ys.getYear() == year)
        .findFirst()
        .orElseGet(
            () -> {
              YearSummary ys = new YearSummary(year);
              trainer.getYearsList().add(ys);
              return ys;
            });
  }

  private MonthSummary getOrCreateMonthSummary(YearSummary yearSummary, String month) {
    return yearSummary.getMonthsList().stream()
        .filter(ms -> ms.getMonth() != null && ms.getMonth().equalsIgnoreCase(month))
        .findFirst()
        .orElseGet(
            () -> {
              MonthSummary ms = new MonthSummary();
              ms.setMonth(month);
              yearSummary.getMonthsList().add(ms);
              return ms;
            });
  }

  private TrainerTrainingSummary findTrainerOrThrow(String username) {
    return repository
        .findByTrainerUsername(username)
        .orElseThrow(
            () ->
                new EntityNotFoundException("No training sessions found for trainer: " + username));
  }

  private YearSummary findYearSummaryOrThrow(
      TrainerTrainingSummary trainer, int year, String trainingName) {
    return trainer.getYearsList().stream()
        .filter(ys -> ys.getYear() != null && ys.getYear() == year)
        .findFirst()
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    "Training session " + trainingName + " not found for removal."));
  }

  private MonthSummary findMonthSummaryOrThrow(
      YearSummary yearSummary, String month, String trainingName) {
    return yearSummary.getMonthsList().stream()
        .filter(ms -> ms.getMonth() != null && ms.getMonth().equalsIgnoreCase(month))
        .findFirst()
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    "Training session " + trainingName + " not found for removal."));
  }

  private void validateDuration(int currentDuration, int sessionDuration, String trainingName) {
    if (currentDuration < sessionDuration) {
      throw new InsufficientDurationException(
          "Training session " + trainingName + " not found for removal: insufficient duration.");
    }
  }

  private void adjustSummaries(
      TrainerTrainingSummary trainer,
      YearSummary yearSummary,
      MonthSummary monthSummary,
      int sessionDuration) {
    monthSummary.setTrainingSummaryDuration(
        monthSummary.getTrainingSummaryDuration() - sessionDuration);

    if (monthSummary.getTrainingSummaryDuration() == 0) {
      yearSummary.getMonthsList().remove(monthSummary);
    }
    if (yearSummary.getMonthsList().isEmpty()) {
      trainer.getYearsList().remove(yearSummary);
    }
  }
}
