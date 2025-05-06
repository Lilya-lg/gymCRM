package uz.micro.gym.cucumber;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import uz.micro.gym.messaging.TrainingMessageProducer;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestMockConfiguration {

  @Bean
  public TrainingMessageProducer trainingMessageProducer() {
    return mock(TrainingMessageProducer.class);
  }
}
