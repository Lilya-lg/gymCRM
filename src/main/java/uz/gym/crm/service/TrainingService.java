package uz.gym.crm.service;

import uz.gym.crm.domain.Training;

public interface TrainingService extends BaseService<Training> {
    void addTraining(Training training);
}
