package uz.gym.crm.dao;

import org.springframework.stereotype.Repository;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;

import java.util.Map;

@Repository
public class TrainingDAOImpl extends BaseDAOImpl<Training> {
    public TrainingDAOImpl(Map<Long, Training> storage) {
        super(storage, Training::getId);
    }

}

