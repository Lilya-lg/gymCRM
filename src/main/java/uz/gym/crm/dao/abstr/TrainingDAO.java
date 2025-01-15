package uz.gym.crm.dao.abstr;

import uz.gym.crm.dao.abstr.BaseDAO;
import uz.gym.crm.domain.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingDAO extends BaseDAO<Training> {
    List<Training> findByCriteria(String traineeUsername, String trainingType, LocalDate fromDate, LocalDate toDate, String trainerName);

    List<Training> findByCriteriaForTrainer(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName);
}
