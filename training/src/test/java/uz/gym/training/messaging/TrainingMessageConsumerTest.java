package uz.gym.training.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.gym.crm.dto.TrainingSessionDTO;
import uz.gym.training.service.TrainerSummaryService;

import static org.mockito.Mockito.*;

class TrainingMessageConsumerTest {

  private TrainingMessageConsumer trainingMessageConsumer;
  private TrainerSummaryService trainerSummaryService;

  @BeforeEach
  void setUp() {
    trainerSummaryService = mock(TrainerSummaryService.class);
    trainingMessageConsumer = new TrainingMessageConsumer(trainerSummaryService);
  }

  @Test
  void receiveTrainingSession_shouldCallAddTraining_whenActionIsAdd() {
    TrainingSessionDTO dto = new TrainingSessionDTO();
    dto.setActionType("ADD");

    trainingMessageConsumer.receiveTrainingSession(dto);

    verify(trainerSummaryService, times(1)).addTraining(dto);
    verify(trainerSummaryService, never()).deleteTraining(any());
  }

  @Test
  void receiveTrainingSession_shouldCallDeleteTraining_whenActionIsDelete() {
    TrainingSessionDTO dto = new TrainingSessionDTO();
    dto.setActionType("DELETE");

    trainingMessageConsumer.receiveTrainingSession(dto);

    verify(trainerSummaryService, times(1)).deleteTraining(dto);
    verify(trainerSummaryService, never()).addTraining(any());
  }

  @Test
  void receiveTrainingSession_shouldDoNothing_whenActionIsUnknown() {
    TrainingSessionDTO dto = new TrainingSessionDTO();
    dto.setActionType("UPDATE");

    trainingMessageConsumer.receiveTrainingSession(dto);

    verify(trainerSummaryService, never()).addTraining(any());
    verify(trainerSummaryService, never()).deleteTraining(any());
  }

  @Test
  void receiveTrainingSession_shouldBeCaseInsensitive() {
    TrainingSessionDTO dto = new TrainingSessionDTO();
    dto.setActionType("add");

    trainingMessageConsumer.receiveTrainingSession(dto);

    verify(trainerSummaryService, times(1)).addTraining(dto);
  }
}
