package uz.micro.gym.dto;

import uz.micro.gym.dto.abstr.BaseTrainerDTO;

public class TrainerDTO extends BaseTrainerDTO {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}