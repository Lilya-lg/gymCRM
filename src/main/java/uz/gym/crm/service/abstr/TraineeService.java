package uz.gym.crm.service.abstr;

import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.dto.TraineeProfileDTO;
import uz.gym.crm.dto.TraineeUpdateDTO;



import java.util.List;
import java.util.Optional;

public interface TraineeService extends BaseService<Trainee> {
    Optional<Trainee> findByUsernameAndPassword(String username, String password, String usernameToMatch, String passwordToMatch);

    void deleteProfileByUsername(String username);

    Optional<Trainee> findByUsername(String username);

    List<Trainer> updateTraineeTrainerList(String username, List<String> trainerIds);

    TraineeProfileDTO getTraineeProfile(String username);

    void updateProfile(String username, Trainee trainee);

    void updateTraineeProfile(String username, TraineeUpdateDTO traineeDTO);

}

