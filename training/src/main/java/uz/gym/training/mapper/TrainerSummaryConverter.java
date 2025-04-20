package uz.gym.training.mapper;

import uz.gym.training.domain.TrainerTrainingSummary;
import uz.gym.training.domain.YearSummary;
import uz.gym.training.domain.MonthSummary;
import uz.gym.training.dto.TrainerSummaryDTO;
import uz.gym.training.dto.MonthSummaryDTO;

import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrainerSummaryConverter {

  private TrainerSummaryConverter() {}

  public static TrainerSummaryDTO toDto(TrainerTrainingSummary trainer) {
    TrainerSummaryDTO dto = new TrainerSummaryDTO();
    dto.setUsername(trainer.getTrainerUsername());
    dto.setFirstName(trainer.getTrainerFirstName());
    dto.setLastName(trainer.getTrainerLastName());
    dto.setStatus(
        Optional.ofNullable(trainer.getTrainerStatus()).map(Object::toString).orElse(null));
    dto.setYears(extractYears(trainer));
    dto.setMonthlySummaries(extractMonthlySummaries(trainer));
    return dto;
  }

  private static List<Integer> extractYears(TrainerTrainingSummary trainer) {
    return trainer.getYearsList().stream().map(YearSummary::getYear).collect(Collectors.toList());
  }

  private static Map<Integer, List<MonthSummaryDTO>> extractMonthlySummaries(
      TrainerTrainingSummary trainer) {
    return trainer.getYearsList().stream()
        .collect(
            Collectors.toMap(
                YearSummary::getYear,
                ys ->
                    ys.getMonthsList().stream()
                        .map(TrainerSummaryConverter::mapMonthToDto)
                        .collect(Collectors.toList())));
  }

  private static MonthSummaryDTO mapMonthToDto(MonthSummary ms) {
    MonthSummaryDTO monthDto = new MonthSummaryDTO();
    monthDto.setMonth(Month.valueOf(ms.getMonth().toUpperCase()));
    monthDto.setTotalTrainingDuration(ms.getTrainingSummaryDuration());
    return monthDto;
  }
}
