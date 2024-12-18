package uz.gym.crm.facade;

import org.springframework.stereotype.Component;
import uz.gym.crm.service.TraineeServiceImpl;
import uz.gym.crm.service.TrainerServiceImpl;
import uz.gym.crm.service.TrainingServiceImpl;

@Component
public class GymFacade {

    private final TraineeServiceImpl traineeService;
    private final TrainerServiceImpl trainerService;

    private TrainingServiceImpl trainingService;

    public GymFacade(TraineeServiceImpl traineeService, TrainerServiceImpl trainerService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    public void setTrainingService(TrainingServiceImpl trainingService) {
        this.trainingService = trainingService;
    }

    public TraineeServiceImpl getTraineeService() {
        return traineeService;
    }

    public TrainerServiceImpl getTrainerService() {
        return trainerService;
    }

    public TrainingServiceImpl getTrainingService() {
        return trainingService;
    }
}

