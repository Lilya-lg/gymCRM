package uz.micro.gym.dto.abstr;

import java.time.LocalDate;

public class BaseTrainingDTO {
    private String trainingName;
    private LocalDate trainingDate;
    private int trainingDuration;

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public LocalDate getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(LocalDate date) {
        this.trainingDate = date;
    }

    public int getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(int trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
}
