package uz.gym.crm.service.abstr;

import uz.gym.crm.domain.Trainee;
import uz.gym.crm.service.abstr.BaseService;


import java.util.List;
import java.util.Optional;

public interface TraineeService extends BaseService<Trainee> {
    Optional<Trainee> findByUsernameAndPassword(String username, String password, String usernameToMatch,String passwordToMatch);

    void deleteProfileByUsername(String username, String userToDelete, String password);

    Optional<Trainee> findByUsername(String username,String password, String usernameToSelect);

    void updateTraineeTrainerList(String username, String password, Long traineeId, List<Long> trainerIds);
}

