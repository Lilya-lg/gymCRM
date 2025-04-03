package uz.micro.gym.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import uz.gym.crm.dto.TrainingSessionDTO;

@Service
public class TrainingMessageProducer {
  private final JmsTemplate jmsTemplate;
  @Autowired ObjectMapper objectMapper;

  public TrainingMessageProducer(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  public void sendTrainingSession(TrainingSessionDTO sessionDTO) {
    jmsTemplate.convertAndSend("training.queue", sessionDTO);
  }
}
