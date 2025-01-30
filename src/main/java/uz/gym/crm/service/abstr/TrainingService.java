package uz.gym.crm.service.abstr;

import uz.gym.crm.domain.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingService extends BaseService<Training> {
    void addTraining(Training training, String username, String password);

    List<Training> findByCriteria(String traineeUsername, String trainingType, LocalDate fromDate, LocalDate toDate, String trainerName);

    List<Training> findByCriteriaForTrainer(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName);

    void linkTraineeTrainer(Training training, String traineeName, String trainerName);

}
