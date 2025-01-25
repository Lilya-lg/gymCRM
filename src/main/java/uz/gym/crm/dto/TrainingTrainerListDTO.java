package uz.gym.crm.dto;

import uz.gym.crm.dto.abstr.BaseTrainingListDTO;

public class TrainingTrainerListDTO extends BaseTrainingListDTO {
    private String traineeName;
    public String getTraineeName() {
        return traineeName;
    }

    public void setTraineeName(String traineeName) {
        this.traineeName = traineeName;
    }
}
