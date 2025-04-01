package uz.micro.gym.dto;

import uz.micro.gym.dto.abstr.BaseTrainingListDTO;

public class TrainingTrainerListDTO extends BaseTrainingListDTO {
    private String traineeName;
    public String getTraineeName() {
        return traineeName;
    }

    public void setTraineeName(String traineeName) {
        this.traineeName = traineeName;
    }
}
