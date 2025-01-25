package uz.gym.crm.dto;

import uz.gym.crm.dto.abstr.BaseTraineeDTO;

import javax.validation.constraints.NotNull;

public class TraineeUpdateDTO extends BaseTraineeDTO {
    @NotNull(message = "Active status is required")
    private String isActive;

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
