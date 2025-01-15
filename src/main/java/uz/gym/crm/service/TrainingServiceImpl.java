package uz.gym.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.gym.crm.dao.TrainerDAOImpl;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.dao.TrainingTypeDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Training;
import uz.gym.crm.domain.TrainingType;
import uz.gym.crm.service.abstr.BaseServiceImpl;
import uz.gym.crm.service.abstr.TrainingService;

import java.time.LocalDate;
import java.util.List;


@Service
public class TrainingServiceImpl extends BaseServiceImpl<Training> implements TrainingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);
    private final TrainingTypeDAOImpl trainingTypeDao;
    private final TrainingDAOImpl trainingDAO;


    public TrainingServiceImpl(TrainingTypeDAOImpl trainingTypeDao, TrainingDAOImpl trainingDAO, UserDAOImpl userDAO) {
        super(trainingDAO, userDAO);
        this.trainingTypeDao = trainingTypeDao;
        this.trainingDAO = trainingDAO;
    }


    public void addTraining(Training training, String username, String password) {
        LOGGER.debug("Adding a new training: {}", training);
        if (!super.authenticate(username, password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        if (training.getTrainingType() != null) {
            TrainingType trainingType = trainingTypeDao.read(training.getTrainingType().getId()).orElseThrow(() -> new IllegalArgumentException("TrainingType not found with ID: " + training.getTrainingType().getId()));
            training.setTrainingType(trainingType);
        }
        getDao().save(training);
        LOGGER.info("Training added successfully with ID: {}", training.getId());
    }

    public List<Training> findByCriteriaForTrainer(String username, String password, String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName) {
        if (!super.authenticate(username, password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        LOGGER.info("Training for trainer with criteria");
        try {
            return trainingDAO.findByCriteriaForTrainer(trainerUsername, fromDate, toDate, traineeName);
        } catch (Exception e) {
            LOGGER.error("Error finding trainings  with criteria", e);
            throw e;
        }

    }

    public List<Training> findByCriteria(String username, String password, String traineeUsername, String trainingType, LocalDate fromDate, LocalDate toDate, String trainerName) {
        if (!super.authenticate(username, password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        LOGGER.info("Training for trainee with criteria");
        try {
            return trainingDAO.findByCriteria(traineeUsername, trainingType, fromDate, toDate, trainerName);
        } catch (Exception e) {
            LOGGER.error("Error finding trainings  with criteria", e);
            throw e;
        }
    }


}

