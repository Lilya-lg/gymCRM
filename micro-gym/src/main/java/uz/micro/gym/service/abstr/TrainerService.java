package uz.micro.gym.service.abstr;

import uz.micro.gym.domain.Trainer;
import uz.micro.gym.domain.User;
import uz.micro.gym.dto.TrainerProfileDTO;
import uz.micro.gym.dto.TrainerProfileResponseDTO;

import java.util.List;

public interface TrainerService extends BaseService<Trainer> {
    List<Trainer> getUnassignedTrainersForTrainee(String traineeUsername);

    TrainerProfileDTO getTrainerProfile(String username);

    TrainerProfileResponseDTO getTrainerProfileResponse(String username);

    void updateTrainerProfile(String username, TrainerProfileDTO trainerDTO);
    String putPassword(User user);
}
