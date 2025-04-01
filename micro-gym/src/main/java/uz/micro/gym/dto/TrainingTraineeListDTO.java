package uz.micro.gym.dto;

import uz.micro.gym.dto.abstr.BaseTrainingListDTO;

public class TrainingTraineeListDTO extends BaseTrainingListDTO {
    private String trainerName;
    private String trainingType;

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public String getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
    }


}
