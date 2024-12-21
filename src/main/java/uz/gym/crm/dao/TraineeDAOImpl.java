package uz.gym.crm.dao;

import org.springframework.stereotype.Repository;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;

import java.util.Map;

@Repository
public class TraineeDAOImpl extends BaseDAOImpl<Trainee> {
    public TraineeDAOImpl(Map<Long, Trainee> storage) {
        super(storage, Trainee::getId);
    }
}
