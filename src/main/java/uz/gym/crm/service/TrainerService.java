package uz.gym.crm.service;

import uz.gym.crm.domain.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerService extends BaseService<Trainer> {
    Optional<Trainer> findByUsernameAndPassword(String username, String password);

    Optional<Trainer> findByUsername(String username);

    List<Trainer> getUnassignedTrainersForTrainee(String traineeUsername);

}
