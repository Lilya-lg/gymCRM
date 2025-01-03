package uz.gym.crm.dao;


import uz.gym.crm.domain.Trainer;

import java.util.List;
import java.util.Optional;


public interface TrainerDAO extends BaseDAO<Trainer> {
    Optional<Trainer> findByUsernameAndPassword(String username, String password);

    List<Trainer> getUnassignedTrainersByTraineeUsername(String traineeUsername);

    Optional<Trainer> findByUsername(String username);
}