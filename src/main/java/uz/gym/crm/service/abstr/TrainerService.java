package uz.gym.crm.service.abstr;

import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;
import uz.gym.crm.dto.TrainerProfileDTO;
import uz.gym.crm.dto.TrainerProfileResponseDTO;

import java.util.List;
import java.util.Optional;

public interface TrainerService extends BaseService<Trainer> {
    List<Trainer> getUnassignedTrainersForTrainee(String traineeUsername);

    TrainerProfileDTO getTrainerProfile(String username);

    TrainerProfileResponseDTO getTrainerProfileResponse(String username);

    void updateTrainerProfile(String username, TrainerProfileDTO trainerDTO);
    String putPassword(User user);
}
