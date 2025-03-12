package uz.gym.crm.service.abstr;

import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;
import uz.gym.crm.dto.TraineeProfileDTO;
import uz.gym.crm.dto.TraineeUpdateDTO;


import java.util.List;


public interface TraineeService extends BaseService<Trainee> {

    void deleteProfileByUsername(String username);

    List<Trainer> updateTraineeTrainerList(String username,Long trainingId, List<String> trainerIds);

    TraineeProfileDTO getTraineeProfile(String username);

    void updateProfile(String username, Trainee trainee);

    void updateTraineeProfile(String username, TraineeUpdateDTO traineeDTO);
    String putPassword(User user);
}

