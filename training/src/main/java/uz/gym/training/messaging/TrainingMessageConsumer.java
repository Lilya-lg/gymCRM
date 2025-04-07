package uz.gym.training.messaging;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import uz.gym.training.service.TrainingService;
import uz.gym.crm.dto.TrainingSessionDTO;

@Service
public class TrainingMessageConsumer {
  private final TrainingService trainingService;
  private static final String ACTION_ADD = "ADD";
  private static final String ACTION_DELETE = "DELETE";

  public TrainingMessageConsumer(TrainingService trainingService) {
    this.trainingService = trainingService;
  }

  @JmsListener(destination = "training.queue", containerFactory = "jmsListenerContainerFactory")
  public void receiveTrainingSession(TrainingSessionDTO dto) {
    if (ACTION_ADD.equalsIgnoreCase(dto.getActionType())) {
      trainingService.addTraining(dto);
    } else if (ACTION_DELETE.equalsIgnoreCase(dto.getActionType())) {
      trainingService.deleteTraining(dto);
    }
  }
}
