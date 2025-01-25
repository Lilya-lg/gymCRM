package uz.gym.crm.dto;

import uz.gym.crm.dto.abstr.BaseTrainerDTO;

import java.util.List;

public class TrainerProfileDTO extends BaseTrainerDTO {

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
