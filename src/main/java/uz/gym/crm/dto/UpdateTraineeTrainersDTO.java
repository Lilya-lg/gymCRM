package uz.gym.crm.dto;

import java.util.List;

public class UpdateTraineeTrainersDTO {
    private List<String> trainerUsernames;

    public List<String> getTrainerUsernames() {
        return trainerUsernames;
    }

    public void setTrainerUsernames(List<String> trainerUsernames) {
        this.trainerUsernames = trainerUsernames;
    }
}
