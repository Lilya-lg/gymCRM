package uz.micro.gym.dto;

import jakarta.validation.constraints.NotNull;
import uz.micro.gym.dto.abstr.BaseTraineeDTO;



public class TraineeUpdateDTO extends BaseTraineeDTO {
    @NotNull(message = "Username is required")
    private String username;

    @NotNull(message = "Active status is required")
    private String isActive;

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
