package uz.micro.gym.dto;

import java.util.List;

public class UpdateTraineeTrainersDTO {
    private String traineeUsername;
    private Long trainingId;
    private List<String> trainerUsernames;

    public Long getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Long trainingId) {
        this.trainingId = trainingId;
    }

    public String getTraineeUsername() {
        return traineeUsername;
    }

    public void setTraineeUsername(String traineeUsername) {
        this.traineeUsername = traineeUsername;
    }

    public List<String> getTrainerUsernames() {
        return trainerUsernames;
    }

    public void setTrainerUsernames(List<String> trainerUsernames) {
        this.trainerUsernames = trainerUsernames;
    }
}
