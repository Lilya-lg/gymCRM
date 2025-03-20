package uz.micro.gym.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import uz.micro.gym.config.FeignConfig;
import uz.micro.gym.dto.TrainingSessionDTO;

@FeignClient(name = "training", configuration = FeignConfig.class)
public interface TrainingServiceClient {
    @PostMapping("/api/trainings")
    void createTraining(@RequestBody TrainingSessionDTO trainingSessionDTO);
}


