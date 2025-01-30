package uz.gym.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.gym.crm.dao.*;
import uz.gym.crm.domain.*;
import uz.gym.crm.service.abstr.BaseServiceImpl;
import uz.gym.crm.service.abstr.TrainingService;

import java.time.LocalDate;
import java.util.List;


@Service
@Transactional
public class TrainingServiceImpl extends BaseServiceImpl<Training> implements TrainingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);
    @Autowired
    TrainingService trainingService;
    private final TrainingTypeDAOImpl trainingTypeDao;
    private final TrainingDAOImpl trainingDAO;
    @Autowired
    private final TraineeDAOImpl traineeDAO;
    @Autowired
    private final TrainerDAOImpl trainerDAO;

    @Autowired
    public TrainingServiceImpl(TrainingTypeDAOImpl trainingTypeDao, TrainingDAOImpl trainingDAO, UserDAOImpl userDAO, TrainerDAOImpl trainerDAO, TraineeDAOImpl traineeDAO) {
        super(trainingDAO, userDAO);
        this.trainingTypeDao = trainingTypeDao;
        this.trainingDAO = trainingDAO;
        this.trainerDAO = trainerDAO;
        this.traineeDAO = traineeDAO;
    }


    public void addTraining(Training training, String username, String password) {
        LOGGER.debug("Adding a new training: {}", training);
        if (!super.authenticate(username, password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        super.create(training);
        LOGGER.info("Training added successfully with ID: {}", training.getId());
    }

    @Override
    public List<Training> findByCriteriaForTrainer(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName) {
        LOGGER.info("Training for trainer with criteria");
        try {
            return trainingDAO.findByCriteriaForTrainer(trainerUsername, fromDate, toDate, traineeName);
        } catch (Exception e) {
            LOGGER.error("Error finding trainings  with criteria", e);
            throw e;
        }

    }

    @Override
    public List<Training> findByCriteria(String traineeUsername, String trainingType, LocalDate fromDate, LocalDate toDate, String trainerName) {
        LOGGER.info("Training for trainee with criteria");
        PredefinedTrainingType trainingType1 = PredefinedTrainingType.fromName(trainingType);
        try {
            return trainingDAO.findByCriteria(traineeUsername, trainingType1, fromDate, toDate, trainerName);
        } catch (Exception e) {
            LOGGER.error("Error finding trainings  with criteria", e);
            throw e;
        }
    }

    @Override
    public void create(Training training) {
        super.create(training);
        LOGGER.info("Training entity created successfully with ID: {}", training.getId());
    }

    @Override
    public void linkTraineeTrainer(Training training, String traineeName, String trainerName) {
        Trainee trainee;
        try {
            trainee = traineeDAO.findByUsername(traineeName).orElseThrow(() -> new IllegalArgumentException("Trainee not found with username: " + traineeName));
        } catch (Exception e) {
            throw new IllegalArgumentException("Trainee not found with username: " + traineeName, e);
        }

        // Retrieve Trainer by username
        Trainer trainer;
        try {
            trainer = trainerDAO.findByUsername(trainerName).orElseThrow(() -> new IllegalArgumentException("Trainer not found with username: " + trainerName));
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


}

