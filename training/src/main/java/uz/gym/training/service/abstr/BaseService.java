package uz.gym.training.service.abstr;


import uz.gym.crm.dto.TrainingSessionDTO;

public interface BaseService {

  void addTraining(TrainingSessionDTO sessionDTO);

  void deleteTraining(TrainingSessionDTO sessionDTO);
}
