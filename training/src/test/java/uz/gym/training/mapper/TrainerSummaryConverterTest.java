package uz.gym.training.mapper;

import org.junit.jupiter.api.Test;
import uz.gym.training.domain.TrainerTrainingSummary;
import uz.gym.training.domain.YearSummary;
import uz.gym.training.domain.MonthSummary;
import uz.gym.training.dto.TrainerSummaryDTO;
import uz.gym.training.dto.MonthSummaryDTO;

import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrainerSummaryConverterTest {

  @Test
  void toDto_basicMapping() {
    TrainerTrainingSummary domain = new TrainerTrainingSummary("user1", "First", "Last", true);
    domain.getYearsList().add(new YearSummary(2022));

    TrainerSummaryDTO dto = TrainerSummaryConverter.toDto(domain);

    assertEquals("user1", dto.getUsername());
    assertEquals("First", dto.getFirstName());
    assertEquals("Last", dto.getLastName());
    assertEquals("true", dto.getStatus());
    assertEquals(List.of(2022), dto.getYears());
  }

  @Test
  void toDto_withMonthlySummaries() {
    TrainerTrainingSummary domain = new TrainerTrainingSummary("user2", "A", "B", false);
    YearSummary year2021 = new YearSummary(2021);
    MonthSummary msJan = new MonthSummary();
    msJan.setMonth("JANUARY");
    msJan.setTrainingSummaryDuration(30);
    MonthSummary msFeb = new MonthSummary();
    msFeb.setMonth("february");
    msFeb.setTrainingSummaryDuration(45);
    year2021.getMonthsList().addAll(Arrays.asList(msJan, msFeb));
    YearSummary year2022 = new YearSummary(2022);
    domain.getYearsList().addAll(Arrays.asList(year2021, year2022));

    TrainerSummaryDTO dto = TrainerSummaryConverter.toDto(domain);

    assertEquals(Arrays.asList(2021, 2022), dto.getYears());

    Map<Integer, List<MonthSummaryDTO>> map = dto.getMonthlySummaries();
    assertTrue(map.containsKey(2021));
    List<MonthSummaryDTO> list2021 = map.get(2021);
    assertEquals(2, list2021.size());

    MonthSummaryDTO dtoJan =
        list2021.stream()
            .filter(d -> d.getMonth() == Month.JANUARY)
            .findFirst()
            .orElseThrow(() -> new AssertionError("January DTO not found"));
    assertEquals(30, dtoJan.getTotalTrainingDuration());

    MonthSummaryDTO dtoFeb =
        list2021.stream()
            .filter(d -> d.getMonth() == Month.FEBRUARY)
            .findFirst()
            .orElseThrow(() -> new AssertionError("February DTO not found"));
    assertEquals(45, dtoFeb.getTotalTrainingDuration());

    assertTrue(map.containsKey(2022));
    assertTrue(map.get(2022).isEmpty(), "Year 2022 should have no month summaries");
  }

  @Test
  void toDto_emptyYears() {

    TrainerTrainingSummary domain = new TrainerTrainingSummary();
    TrainerSummaryDTO dto = TrainerSummaryConverter.toDto(domain);
    assertTrue(dto.getYears().isEmpty(), "Years list should be empty");
    assertTrue(dto.getMonthlySummaries().isEmpty(), "Monthly summaries should be empty");
  }
}
