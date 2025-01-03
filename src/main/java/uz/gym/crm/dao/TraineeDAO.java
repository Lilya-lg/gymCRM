package uz.gym.crm.dao;

import uz.gym.crm.domain.Trainee;

import java.util.Optional;
import java.util.Set;

public interface TraineeDAO extends BaseDAO<Trainee> {
    Optional<Trainee> findByUsernameAndPassword(String username, String password);

    void updateTraineeTrainerList(Long traineeId, Set<Long> newTrainerIds);

    Optional<Trainee> findByUsername(String username);
}
