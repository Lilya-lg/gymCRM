package uz.gym.crm.service;

import uz.gym.crm.domain.Trainee;


import java.util.Optional;

public interface TraineeService extends BaseService<Trainee> {
    Optional<Trainee> findByUsernameAndPassword(String username, String password);

    void deleteProfileByUsername(String username, String password);

    Optional<Trainee> findByUsername(String username);
}

