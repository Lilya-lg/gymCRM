package uz.gym.crm.dto;

import uz.gym.crm.dto.abstr.BaseTrainingDTO;

public class TrainingDTO extends BaseTrainingDTO {
    private String traineeUsername;
    private String trainerUsername;

    public String getTraineeUsername() {
        return traineeUsername;
    }

    public void setTraineeUsername(String traineeUsername) {
        this.traineeUsername = traineeUsername;
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }
}
