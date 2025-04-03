package uz.gym.training;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class TrainingApplication {

  public static void main(String[] args) {

    SpringApplication.run(TrainingApplication.class, args);
  }
}
