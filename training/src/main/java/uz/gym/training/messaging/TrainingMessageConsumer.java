package uz.gym.training.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import uz.gym.training.service.TrainingService;
import uz.gym.crm.dto.TrainingSessionDTO;

@Service
public class TrainingMessageConsumer {
  private final TrainingService trainingService;

  public TrainingMessageConsumer(TrainingService trainingService) {
    this.trainingService = trainingService;
  }

  @JmsListener(destination = "training.queue", containerFactory = "jmsListenerContainerFactory")
  public void receiveTrainingSession(TrainingSessionDTO dto) {
    if ("ADD".equalsIgnoreCase(dto.getActionType())) {
      trainingService.addTraining(dto);
    } else if ("DELETE".equalsIgnoreCase(dto.getActionType())) {
      trainingService.deleteTraining(dto);
    }
  }
}
