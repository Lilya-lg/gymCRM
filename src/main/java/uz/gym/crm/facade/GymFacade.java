package uz.gym.crm.facade;

import org.springframework.stereotype.Component;
import uz.gym.crm.service.TraineeService;
import uz.gym.crm.service.TrainerService;
import uz.gym.crm.service.TrainingService;

@Component
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;

    private TrainingService trainingService; // Setter-based injection

    public GymFacade(TraineeService traineeService, TrainerService trainerService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    public void setTrainingService(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    public TraineeService getTraineeService() {
        return traineeService;
    }

    public TrainerService getTrainerService() {
        return trainerService;
    }

    public TrainingService getTrainingService() {
        return trainingService;
    }
}

