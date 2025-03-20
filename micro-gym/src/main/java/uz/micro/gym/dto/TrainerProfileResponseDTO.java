package uz.micro.gym.dto;

import uz.micro.gym.dto.abstr.BaseTrainerDTO;

import java.util.List;

public class TrainerProfileResponseDTO extends BaseTrainerDTO {
    private boolean isActive;
    private List<UserDTO> trainees;


    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public List<UserDTO> getTrainees() {
        return trainees;
    }

    public void setTrainees(List<UserDTO> trainees) {
        this.trainees = trainees;
    }
}
