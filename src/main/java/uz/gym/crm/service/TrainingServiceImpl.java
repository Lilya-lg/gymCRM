package uz.gym.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.gym.crm.dao.TraineeDAOImpl;
import uz.gym.crm.dao.TrainerDAOImpl;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.dao.TrainingTypeDAOImpl;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;
import uz.gym.crm.domain.TrainingType;


@Service
public class TrainingServiceImpl extends BaseServiceImpl<Training> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);
    private final TraineeDAOImpl traineeDAO;
    private final TrainerDAOImpl trainerDAO;
    private final TrainingTypeDAOImpl trainingTypeDao;
    private final TrainingDAOImpl trainingDAO;

    public TrainingServiceImpl(TraineeDAOImpl traineeDAO,
                               TrainerDAOImpl trainerDAO,
                               TrainingTypeDAOImpl trainingTypeDao,
                               TrainingDAOImpl trainingDAO) {
        super(trainingDAO);
        this.trainingDAO = trainingDAO;
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
        this.trainingTypeDao = trainingTypeDao;
    }

    //16. add training
    public void addTraining(Training training) {
        LOGGER.debug("Adding a new training: {}", training);

        Trainee trainee = traineeDAO.read(training.getTrainee().getId())
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found with ID: " + training.getTrainee().getId()));


        Trainer trainer = trainerDAO.read(training.getTrainer().getId())
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with ID: " + training.getTrainer().getId()));


        if (training.getTrainingType() != null) {
            TrainingType trainingType = trainingTypeDao.read(training.getTrainingType().getId())
                    .orElseThrow(() -> new IllegalArgumentException("TrainingType not found with ID: " + training.getTrainingType().getId()));
            training.setTrainingType(trainingType);
        }

        training.setTrainee(trainee);
        training.setTrainer(trainer);

        // Save Training
        dao.save(training);
        LOGGER.info("Training added successfully with ID: {}", training.getId());
    }


}

