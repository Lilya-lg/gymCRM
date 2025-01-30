package uz.gym.crm.dto;

import uz.gym.crm.dto.abstr.BaseTraineeDTO;

import java.util.List;

public class TraineeProfileDTO extends BaseTraineeDTO {
    private boolean isActive;
    private List<TrainerDTO> trainers;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<TrainerDTO> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<TrainerDTO> trainers) {
        this.trainers = trainers;
    }
}
