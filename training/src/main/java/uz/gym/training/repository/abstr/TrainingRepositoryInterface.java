package uz.gym.training.repository.abstr;

import uz.gym.training.domain.TrainingSession;

import java.util.List;

public interface TrainingRepositoryInterface {
    void addTraining(String trainerUsername, TrainingSession session);
    void removeTraining(String trainerUsername, TrainingSession session);
    List<TrainingSession> getTrainingsByTrainer(String trainerUsername);
    boolean trainerExists(String trainerUsername);
}