package uz.gym.crm.service.abstr;

import uz.gym.crm.domain.Trainer;
import uz.gym.crm.service.abstr.BaseService;

import java.util.List;
import java.util.Optional;

public interface TrainerService extends BaseService<Trainer> {
    Optional<Trainer> findByUsernameAndPassword(String username, String password, String usernameToMatch,String passwordToMatch);

    Optional<Trainer> findByUsername(String username,String password,String usernameToSelect);

    List<Trainer> getUnassignedTrainersForTrainee(String traineeUsername,String username,String password);

}
