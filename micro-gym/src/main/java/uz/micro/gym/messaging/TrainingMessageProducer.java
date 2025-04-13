package uz.micro.gym.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import uz.gym.crm.dto.TrainingSessionDTO;

@Service
public class TrainingMessageProducer {
  private final JmsTemplate jmsTemplate;
  private final ObjectMapper objectMapper;
  @Autowired
  public TrainingMessageProducer(JmsTemplate jmsTemplate,ObjectMapper objectMapper) {
    this.jmsTemplate = jmsTemplate;
    this.objectMapper = objectMapper;
  }

  public void sendTrainingSession(TrainingSessionDTO sessionDTO) {
    jmsTemplate.convertAndSend("training.queue", sessionDTO);
  }
}
