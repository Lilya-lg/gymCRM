package uz.micro.gym.service.abstr;

import uz.micro.gym.domain.Trainee;
import uz.micro.gym.domain.Trainer;
import uz.micro.gym.domain.User;
import uz.micro.gym.dto.TraineeProfileDTO;
import uz.micro.gym.dto.TraineeUpdateDTO;

import java.util.List;


public interface TraineeService extends BaseService<Trainee> {

    void deleteProfileByUsername(String username);

    List<Trainer> updateTraineeTrainerList(String username, Long trainingId, List<String> trainerIds);

    TraineeProfileDTO getTraineeProfile(String username);

    void updateProfile(String username, Trainee trainee);

    void updateTraineeProfile(String username, TraineeUpdateDTO traineeDTO);
    String putPassword(User user);
}

