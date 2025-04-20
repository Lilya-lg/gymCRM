package uz.gym.training.service.abstr;

import uz.gym.crm.dto.TrainingSessionDTO;
import uz.gym.training.dto.TrainerSummaryDTO;

public interface BaseService {

  void getOrCreateTraining(TrainingSessionDTO sessionDTO);

  void deleteTraining(TrainingSessionDTO sessionDTO);

  TrainerSummaryDTO getTrainerSummary(String trainerUsername);
}
