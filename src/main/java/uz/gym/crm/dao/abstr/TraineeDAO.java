package uz.gym.crm.dao.abstr;

import uz.gym.crm.domain.Trainee;

import java.util.List;
import java.util.Optional;


public interface TraineeDAO extends BaseDAO<Trainee> {
    Optional<Trainee> findByUsernameAndPassword(String username, String password);

    void updateTraineeTrainerList(Long traineeId, List<Long> newTrainerIds);

    Optional<Trainee> findByUsername(String username);
}
