package uz.gym.training.messaging;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import uz.gym.training.service.TrainerSummaryService;
import uz.gym.crm.dto.*;

@Service
public class TrainingMessageConsumer {
  private final TrainerSummaryService trainerSummaryService;
  private static final String ACTION_ADD = "ADD";
  private static final String ACTION_DELETE = "DELETE";

  public TrainingMessageConsumer(TrainerSummaryService trainerSummaryService) {
    this.trainerSummaryService = trainerSummaryService;
  }

  @JmsListener(destination = "training.queue", containerFactory = "jmsListenerContainerFactory")
  public void receiveTrainingSession(TrainingSessionDTO dto) {
    if (ACTION_ADD.equalsIgnoreCase(dto.getActionType())) {
      trainerSummaryService.getOrCreateTraining(dto);
    } else if (ACTION_DELETE.equalsIgnoreCase(dto.getActionType())) {
      trainerSummaryService.deleteTraining(dto);
    }
  }

}
