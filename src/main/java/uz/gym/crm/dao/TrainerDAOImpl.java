package uz.gym.crm.dao;


import org.springframework.stereotype.Repository;
import uz.gym.crm.domain.Trainer;

import java.util.Map;

@Repository
public class TrainerDAOImpl extends BaseDAOImpl<Trainer> {
    public TrainerDAOImpl(Map<Long, Trainer> storage) {
        super(storage, Trainer::getId);
    }
}
