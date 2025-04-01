package uz.micro.gym.dto;

import jakarta.validation.constraints.NotNull;
import uz.micro.gym.dto.abstr.BaseTrainerDTO;

import java.util.List;

public class TrainerProfileDTO extends BaseTrainerDTO {
    @NotNull(message = "Username is required")
    private String username;
    @NotNull(message = "Active status is required")
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
