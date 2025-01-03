package uz.gym.crm.dao;

import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingDAO extends BaseDAO<Training> {
    List<Training> findByCriteria(Trainee trainee, String trainer, LocalDate fromDate, LocalDate toDate);

    List<Training> findByCriteriaForTrainer(Trainer trainer, String trainerName, String trainingType, LocalDate fromDate, LocalDate toDate);
}
