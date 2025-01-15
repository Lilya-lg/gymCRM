package uz.gym.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.gym.crm.dao.TraineeDAOImpl;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.User;
import uz.gym.crm.service.abstr.AbstractProfileService;
import uz.gym.crm.service.abstr.BaseServiceImpl;
import uz.gym.crm.service.abstr.TraineeService;

import java.util.List;
import java.util.Optional;


@Service
public class TraineeServiceImpl extends AbstractProfileService<Trainee> implements TraineeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private final TraineeDAOImpl traineeDAO;



    public TraineeServiceImpl(UserDAOImpl userDAO, TraineeDAOImpl traineeDAO, TrainingDAOImpl trainingDAO) {
        super(traineeDAO, userDAO, trainingDAO);
        this.traineeDAO = traineeDAO;
    }


    @Override
    public void create(Trainee trainee) {
        prepareUser(trainee.getUser());
        super.create(trainee);
        LOGGER.info("Trainee entity created successfully with ID: {}", trainee.getId());
    }

    @Override
    public void deleteProfileByUsername(String username, String userToDelete, String password) {
        LOGGER.debug("Deleting Trainee profile with username: {}", username);
        if (!super.authenticate(username, password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        traineeDAO.deleteByUsername(userToDelete);
        LOGGER.info("Trainee profile and associated user deleted successfully for username: {}", username);
    }


    public Optional<Trainee> findByUsername(String username, String password, String usernameToSelect) {
        if (!super.authenticate(username, password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        LOGGER.debug("Searching for profile with username: {}", usernameToSelect);
        return  getDao().findByUsername(usernameToSelect);
    }


    public Optional<Trainee> findByUsernameAndPassword(String usernameAuth, String passwordAuth,String username, String password) {
        if (!super.authenticate(usernameAuth, passwordAuth)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        LOGGER.debug("Attempting to find trainer with username: {}", username);
        try {
            return traineeDAO.findByUsernameAndPassword(username, password);
        } catch (Exception e) {
            LOGGER.error("Error finding trainer with username: {}", username, e);
            throw e;
        }
    }

    @Override
    public void updateTraineeTrainerList(String username, String password, Long traineeId, List<Long> trainerIds) {
        if (!super.authenticate(username, password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        traineeDAO.updateTraineeTrainerList(traineeId, trainerIds);
    }

    @Override
    protected User getUser(Trainee entity) {
        return entity.getUser();
    }


}
