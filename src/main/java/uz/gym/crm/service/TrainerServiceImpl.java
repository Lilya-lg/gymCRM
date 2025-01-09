package uz.gym.crm.service;

import org.springframework.stereotype.Service;
import uz.gym.crm.dao.TrainerDAOImpl;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.gym.crm.domain.User;
import uz.gym.crm.service.abstr.AbstractProfileService;
import uz.gym.crm.service.abstr.TrainerService;

import java.util.List;
import java.util.Optional;


@Service
public class TrainerServiceImpl extends AbstractProfileService<Trainer> implements TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private final TrainerDAOImpl trainerDAO;

    public TrainerServiceImpl(UserDAOImpl userDAO, TrainerDAOImpl trainerDAO, TrainingDAOImpl trainingDAO) {
        super(trainerDAO, userDAO, trainingDAO);
        this.trainerDAO = trainerDAO;
    }


    @Override
    public void create(Trainer trainer) {
        prepareUser(trainer.getUser());
        super.create(trainer);
        LOGGER.info("Trainer entity created successfully with ID: {}", trainer.getId());
    }


    public Optional<Trainer> findByUsernameAndPassword(String usernameAuth, String passwordAuth,String username, String password) {
        if (!super.authenticate(usernameAuth, passwordAuth)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        LOGGER.debug("Attempting to find trainer with username: {} and password: {}", username, password);
        try {
            return trainerDAO.findByUsernameAndPassword(username, password);
        } catch (Exception e) {
            LOGGER.error("Error finding trainer with username: {} and password: {}", username, password, e);
            throw e;
        }
    }


    public Optional<Trainer> findByUsername(String username,String password,String usernameToSelect) {
        if (!super.authenticate(username, password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        LOGGER.debug("Searching for profile with username: {}", usernameToSelect);
        return dao.findByUsername(usernameToSelect);
    }


    public List<Trainer> getUnassignedTrainersForTrainee(String traineeUsername, String username, String password) {
        LOGGER.debug("Fetching unassigned trainers for trainee with username: {}", traineeUsername);
        if (!super.authenticate(username, password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        List<Trainer> unassignedTrainers = trainerDAO.getUnassignedTrainersByTraineeUsername(traineeUsername);

        LOGGER.info("Found {} unassigned trainers for trainee with username: {}", unassignedTrainers.size(), traineeUsername);
        return unassignedTrainers;
    }


    @Override
    protected User getUser(Trainer entity) {
        return entity.getUser();
    }


}
