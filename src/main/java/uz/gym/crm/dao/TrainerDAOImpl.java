package uz.gym.crm.dao;


import org.springframework.stereotype.Repository;
import uz.gym.crm.domain.Trainer;

import java.util.Map;

@Repository
public class TrainerDAOImpl extends BaseDAOImpl<Trainer, Integer> implements TrainerDAO {
    public TrainerDAOImpl(Map<Integer, Trainer> trainerStorage) {
        super(Trainer::getId);
        this.storage.putAll(trainerStorage); // Populate storage
    }
}
