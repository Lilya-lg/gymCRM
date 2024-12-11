package uz.gym.crm.dao;

import org.springframework.stereotype.Repository;
import uz.gym.crm.domain.Trainee;

import java.util.Map;

@Repository
public class TraineeDAOImpl extends BaseDAOImpl<Trainee, Integer> implements TraineeDAO {
    public TraineeDAOImpl(Map<Integer, Trainee> traineeStorage) {
        super(Trainee::getId);
        this.storage.putAll(traineeStorage); // Populate storage
    }
}
