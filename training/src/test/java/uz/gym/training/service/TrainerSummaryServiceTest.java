package uz.gym.training.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import uz.gym.crm.dto.TrainingSessionDTO;
import uz.gym.training.domain.MonthSummary;
import uz.gym.training.domain.TrainerTrainingSummary;
import uz.gym.training.domain.YearSummary;
import uz.gym.training.dto.MonthSummaryDTO;
import uz.gym.training.dto.TrainerSummaryDTO;
import uz.gym.training.repository.abstr.TrainerTrainingSummaryRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerSummaryServiceTest {

  @Mock private TrainerTrainingSummaryRepository repository;

  @InjectMocks private TrainerSummaryService service;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testAddTraining_NewTrainer() {
    TrainingSessionDTO dto = new TrainingSessionDTO();
    dto.setUsername("trainer1");
    dto.setFirstName("Alice");
    dto.setLastName("Smith");
    dto.setActive(true);
    dto.setTrainingDate(LocalDate.of(2025, 4, 13));
    dto.setDuration(60);

    when(repository.findByTrainerUsername("trainer1")).thenReturn(Optional.empty());

    service.addTraining(dto);
    ArgumentCaptor<TrainerTrainingSummary> captor =
        ArgumentCaptor.forClass(TrainerTrainingSummary.class);
    verify(repository, times(1)).save(captor.capture());

    TrainerTrainingSummary savedTrainer = captor.getValue();
    assertEquals("trainer1", savedTrainer.getTrainerUsername());
    assertEquals("Alice", savedTrainer.getTrainerFirstName());
    assertEquals("Smith", savedTrainer.getTrainerLastName());
    assertTrue(savedTrainer.getTrainerStatus());

    List<YearSummary> yearsList = savedTrainer.getYearsList();
    assertEquals(1, yearsList.size());
    YearSummary yearSummary = yearsList.get(0);
    assertEquals(2025, yearSummary.getYear().intValue());
    List<MonthSummary> monthsList = yearSummary.getMonthsList();
    assertEquals(1, monthsList.size());
    MonthSummary monthSummary = monthsList.get(0);
    // The month is stored as a String; for April, it could be "APRIL"
    assertEquals("APRIL", monthSummary.getMonth().toUpperCase());
    assertEquals(60, monthSummary.getTrainingSummaryDuration());
  }

  @Test
  public void testAddTraining_ExistingTrainer_UpdateDuration() {
    TrainerTrainingSummary existingTrainer =
        new TrainerTrainingSummary("trainer1", "Alice", "Smith", true);
    YearSummary yearSummary = new YearSummary(2025);
    MonthSummary monthSummary = new MonthSummary();
    monthSummary.setMonth("APRIL");
    monthSummary.setTrainingSummaryDuration(30);
    yearSummary.getMonthsList().add(monthSummary);
    existingTrainer.getYearsList().add(yearSummary);

    TrainingSessionDTO dto = new TrainingSessionDTO();
    dto.setUsername("trainer1");
    dto.setFirstName("Alice");
    dto.setLastName("Smith");
    dto.setActive(true);
    dto.setTrainingDate(LocalDate.of(2025, 4, 20));
    dto.setDuration(45);

    when(repository.findByTrainerUsername("trainer1")).thenReturn(Optional.of(existingTrainer));

    service.addTraining(dto);
    ArgumentCaptor<TrainerTrainingSummary> captor =
        ArgumentCaptor.forClass(TrainerTrainingSummary.class);
    verify(repository, times(1)).save(captor.capture());
    TrainerTrainingSummary savedTrainer = captor.getValue();

    YearSummary updatedYear = savedTrainer.getYearsList().get(0);
    MonthSummary updatedMonth = updatedYear.getMonthsList().get(0);
    assertEquals(75, updatedMonth.getTrainingSummaryDuration());
  }

  @Test
  public void testGetTrainerSummary_Found() {
    TrainerTrainingSummary trainer = new TrainerTrainingSummary("trainer1", "Alice", "Smith", true);
    YearSummary yearSummary = new YearSummary(2025);
    MonthSummary monthSummary = new MonthSummary();
    monthSummary.setMonth("APRIL");
    monthSummary.setTrainingSummaryDuration(90);
    yearSummary.getMonthsList().add(monthSummary);
    trainer.getYearsList().add(yearSummary);

    when(repository.findByTrainerUsername("trainer1")).thenReturn(Optional.of(trainer));

    TrainerSummaryDTO summary = service.getTrainerSummary("trainer1");

    assertNotNull(summary);
    assertEquals("trainer1", summary.getUsername());
    assertEquals("Alice", summary.getFirstName());
    assertEquals("Smith", summary.getLastName());
    assertEquals("true", summary.getStatus());
    assertTrue(summary.getYears().contains(2025));
    Map<Integer, List<MonthSummaryDTO>> monthlySummaries = summary.getMonthlySummaries();
    assertTrue(monthlySummaries.containsKey(2025));
    List<MonthSummaryDTO> monthDtoList = monthlySummaries.get(2025);
    assertEquals(1, monthDtoList.size());
    MonthSummaryDTO msDto = monthDtoList.get(0);
    assertEquals(Month.APRIL, msDto.getMonth());
    assertEquals(90, msDto.getTotalTrainingDuration());
  }

  @Test
  public void testGetTrainerSummary_NotFound() {
    when(repository.findByTrainerUsername("nonexistent")).thenReturn(Optional.empty());
    TrainerSummaryDTO summary = service.getTrainerSummary("nonexistent");
    assertNull(summary);
  }

  // Tests for deleteTraining()
  @Test
  public void testDeleteTraining_Success_PartialRemoval() {
    // Create trainer with a YearSummary/MonthSummary.
    TrainerTrainingSummary trainer = new TrainerTrainingSummary("trainer1", "Alice", "Smith", true);
    YearSummary yearSummary = new YearSummary(2025);
    MonthSummary monthSummary = new MonthSummary();
    monthSummary.setMonth("APRIL");
    monthSummary.setTrainingSummaryDuration(100);
    yearSummary.getMonthsList().add(monthSummary);
    trainer.getYearsList().add(yearSummary);

    when(repository.findByTrainerUsername("trainer1")).thenReturn(Optional.of(trainer));

    TrainingSessionDTO dto = new TrainingSessionDTO();
    dto.setUsername("trainer1");
    dto.setTrainingDate(LocalDate.of(2025, 4, 15));
    dto.setDuration(40);

    service.deleteTraining(dto);

    assertEquals(60, monthSummary.getTrainingSummaryDuration());
    verify(repository, times(1)).save(trainer);
  }

  @Test
  public void testDeleteTraining_Success_FullRemoval() {
    TrainerTrainingSummary trainer = new TrainerTrainingSummary("trainer1", "Alice", "Smith", true);
    YearSummary yearSummary = new YearSummary(2025);
    MonthSummary monthSummary = new MonthSummary();
    monthSummary.setMonth("APRIL");
    monthSummary.setTrainingSummaryDuration(50);
    yearSummary.getMonthsList().add(monthSummary);
    trainer.getYearsList().add(yearSummary);

    when(repository.findByTrainerUsername("trainer1")).thenReturn(Optional.of(trainer));

    TrainingSessionDTO dto = new TrainingSessionDTO();
    dto.setUsername("trainer1");
    dto.setTrainingDate(LocalDate.of(2025, 4, 10));
    dto.setDuration(50);

    service.deleteTraining(dto);

    assertTrue(yearSummary.getMonthsList().isEmpty());
    assertTrue(trainer.getYearsList().isEmpty());

    verify(repository, times(1)).save(trainer);
  }

  @Test
  public void testDeleteTraining_TrainerNotFound() {
    when(repository.findByTrainerUsername("trainer1")).thenReturn(Optional.empty());
    TrainingSessionDTO dto = new TrainingSessionDTO();
    dto.setUsername("trainer1");
    dto.setTrainingDate(LocalDate.of(2025, 4, 10));
    dto.setDuration(30);

    EntityNotFoundException ex =
        assertThrows(EntityNotFoundException.class, () -> service.deleteTraining(dto));
    assertTrue(ex.getMessage().contains("No training sessions found for trainer: trainer1"));
  }

  @Test
  public void testDeleteTraining_InsufficientDuration() {
    TrainerTrainingSummary trainer = new TrainerTrainingSummary("trainer1", "Alice", "Smith", true);
    YearSummary yearSummary = new YearSummary(2025);
    MonthSummary monthSummary = new MonthSummary();
    monthSummary.setMonth("APRIL");
    monthSummary.setTrainingSummaryDuration(30);
    yearSummary.getMonthsList().add(monthSummary);
    trainer.getYearsList().add(yearSummary);

    when(repository.findByTrainerUsername("trainer1")).thenReturn(Optional.of(trainer));

    TrainingSessionDTO dto = new TrainingSessionDTO();
    dto.setUsername("trainer1");
    dto.setTrainingDate(LocalDate.of(2025, 4, 20));
    dto.setDuration(50);

    EntityNotFoundException ex =
        assertThrows(EntityNotFoundException.class, () -> service.deleteTraining(dto));
    assertTrue(ex.getMessage().contains("insufficient duration"));
  }
}
