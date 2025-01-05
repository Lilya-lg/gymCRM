package uz.gym.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.gym.crm.dao.TraineeDAOImpl;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.User;

import java.util.Optional;


@Service
public class TraineeServiceImpl extends AbstractProfileService<Trainee> implements TraineeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private final UserDAOImpl userDAO;
    private final TraineeDAOImpl traineeDAO;
    private final TrainingDAOImpl trainingDAO;


    public TraineeServiceImpl(UserDAOImpl userDAO, TraineeDAOImpl traineeDAO, TrainingDAOImpl trainingDAO) {
        super(traineeDAO, userDAO, trainingDAO);
        this.userDAO = userDAO;
        this.traineeDAO = traineeDAO;
        this.trainingDAO = trainingDAO;
    }


    @Override
    public void create(Trainee trainee) {
        prepareUser(trainee.getUser());
        super.create(trainee);
        LOGGER.info("Trainee entity created successfully with ID: {}", trainee.getId());
    }


    public void deleteProfileByUsername(String username, String password) {
        LOGGER.debug("Deleting Trainee profile with username: {}", username);

        super.authenticate(username, password);

        Trainee trainee = traineeDAO.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found for username: " + username));

        User user = trainee.getUser();
        dao.delete(trainee.getId());
        LOGGER.info("Trainee profile and associated user deleted successfully for username: {}", username);
    }

    public Optional<Trainee> findByUsername(String username) {
        LOGGER.debug("Searching for profile with username: {}", username);
        return dao.findByUsername(username);
    }


    public Optional<Trainee> findByUsernameAndPassword(String username, String password) {
        LOGGER.debug("Attempting to find trainer with username: {} and password: {}", username, password);
        try {
            return traineeDAO.findByUsernameAndPassword(username, password);
        } catch (Exception e) {
            LOGGER.error("Error finding trainer with username: {} and password: {}", username, password, e);
            throw e;
        }
    }


    @Override
    protected User getUser(Trainee entity) {
        return entity.getUser();
    }


}
