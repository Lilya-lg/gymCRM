package uz.gym.crm.service.abstr;

import uz.gym.crm.domain.Trainer;
import uz.gym.crm.dto.TrainerProfileDTO;

import java.util.List;
import java.util.Optional;

public interface TrainerService extends BaseService<Trainer> {
    Optional<Trainer> findByUsernameAndPassword(String username, String password, String usernameToMatch, String passwordToMatch);

    Optional<Trainer> findByUsername(String username, String password, String usernameToSelect);

    List<Trainer> getUnassignedTrainersForTrainee(String traineeUsername);

    TrainerProfileDTO getTrainerProfile(String username);


    void updateTrainerProfile(String username, TrainerProfileDTO trainerDTO);

    void activate(String username);

}
