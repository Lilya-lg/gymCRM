package uz.gym.crm.dao;

import org.springframework.stereotype.Repository;
import uz.gym.crm.domain.Training;

import java.util.Map;

@Repository
public class TrainingDAOImpl extends BaseDAOImpl<Training, Integer> implements TrainingDAO {
    public TrainingDAOImpl(Map<Integer, Training> trainingStorage) {
        super(Training::getId);
        this.storage.putAll(trainingStorage); // Populate storage
    }

}

