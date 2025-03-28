package uz.gym.training.service.abstr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.gym.training.dto.TrainingSessionDTO;

import java.util.concurrent.atomic.AtomicLong;

public interface BaseService {
  Logger LOGGER = LoggerFactory.getLogger(BaseService.class);
  AtomicLong trainerIdCounter = new AtomicLong(1);
  AtomicLong sessionIdCounter = new AtomicLong(1);

  void addTraining(TrainingSessionDTO sessionDTO);

  void deleteTraining(TrainingSessionDTO sessionDTO);
}
