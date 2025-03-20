package uz.micro.gym.service.abstr;


import uz.micro.gym.domain.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingService {
    void addTraining(Training training, String username, String password);

    List<Training> findByCriteria(String traineeUsername, String trainingType, LocalDate fromDate, LocalDate toDate, String trainerName);

    List<Training> findByCriteriaForTrainer(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName);

    void linkTraineeTrainer(Training training, String traineeName, String trainerName);

    void create(Training training);
}
