package uz.gym.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.gym.crm.domain.*;
import uz.gym.crm.repository.TraineeRepository;
import uz.gym.crm.repository.TrainerRepository;
import uz.gym.crm.repository.TrainingRepository;
import uz.gym.crm.service.abstr.TrainingService;

import java.time.LocalDate;
import java.util.List;


@Service
@Transactional
public class TrainingServiceImpl implements TrainingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);
    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;


    public TrainingServiceImpl(TrainingRepository trainingRepository, TrainerRepository trainerRepository, TraineeRepository traineeRepository) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }


    public void addTraining(Training training, String username, String password) {
        LOGGER.debug("Adding a new training: {}", training);
        trainingRepository.save(training);
        LOGGER.info("Training added successfully with ID: {}", training.getId());
    }


    public List<Training> findByCriteriaForTrainer(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName) {
        LOGGER.info("Training for trainer with criteria");
        try {
            return trainingRepository.findByCriteriaForTrainer(trainerUsername, fromDate, toDate, traineeName);
        } catch (Exception e) {
            LOGGER.error("Error finding trainings  with criteria", e);
            throw e;
        }

    }


    public List<Training> findByCriteria(String traineeUsername, String trainingType, LocalDate fromDate, LocalDate toDate, String trainerName) {
        LOGGER.info("Training for trainee with criteria");
        PredefinedTrainingType trainingType1 = PredefinedTrainingType.fromName(trainingType);
        try {
            return trainingRepository.findByCriteria(traineeUsername, trainingType1, fromDate, toDate, trainerName);
        } catch (Exception e) {
            LOGGER.error("Error finding trainings  with criteria", e);
            throw e;
        }
    }


    public void linkTraineeTrainer(Training training, String traineeName, String trainerName) {
        Trainee trainee;
        try {
            trainee = traineeRepository.findByUsername(traineeName).orElseThrow(() -> new IllegalArgumentException("Trainee not found with username: " + traineeName));
        } catch (Exception e) {
            throw new IllegalArgumentException("Trainee not found with username: " + traineeName, e);
        }

        // Retrieve Trainer by username
        Trainer trainer;
        try {
            trainer = trainerRepository.findByUsername(trainerName).orElseThrow(() -> new IllegalArgumentException("Trainer not found with username: " + trainerName));
        } catch (Exception e) {
            throw new IllegalArgumentException("Trainer not found with username: " + trainerName, e);
        }

        // Link the Trainee and Trainer to the Training
        try {
            training.setTrainer(trainer);
            training.setTrainingType(trainer.getSpecialization());
            training.setTrainee(trainee);
        } catch (Exception e) {
            throw new IllegalStateException("Error while linking Trainee and Trainer to Training", e);
        }

    }

    @Override
    public void create(Training training) {
        trainingRepository.save(training);
    }


}

