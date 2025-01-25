package uz.gym.crm.dao.abstr;
import uz.gym.crm.domain.PredefinedTrainingType;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingDAO extends BaseDAO<Training> {
    List<Training> findByCriteria(String traineeUsername, PredefinedTrainingType trainingType, LocalDate fromDate, LocalDate toDate, String trainerName);

    List<Training> findByCriteriaForTrainer(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName);

    List<Trainer> findTrainersByTraineeId(Long traineeId);

    List<Trainee> findTraineesByTrainerId(Long trainerId);
}
