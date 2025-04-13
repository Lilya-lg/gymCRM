package uz.micro.gym.service;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import uz.micro.gym.dto.TrainingSessionDTO;


public interface TrainingServiceClient {
    @PostMapping("/api/trainings")
    void createTraining(@RequestBody TrainingSessionDTO trainingSessionDTO);
}




