package uz.gym.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.gym.crm.dao.BaseDAO;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.User;

import java.util.Optional;

@Service
public class TraineeServiceImpl extends AbstractProfileService<Trainee> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private final UserDAOImpl userDAO;

    public TraineeServiceImpl(BaseDAO<Trainee> traineeDAO, UserDAOImpl userDAO) {
        super(traineeDAO, userDAO);
        this.userDAO = userDAO;
    }

    @Override
    public void create(Trainee trainee) {
        Long userId = trainee.getUserId();
        User user = resolveUser(userId);
        prepareUser(user);
        if (user.getId() == 0) {
            userDAO.create(user);
        }
        trainee.setUserId(user.getId());
        super.create(trainee);
        LOGGER.info("Trainee entity created successfully with ID: {}", trainee.getId());
    }

    private User resolveUser(Long userId) {
        Optional<User> userOptional = userDAO.findById(userId);
        if (userOptional.isEmpty()) {
            LOGGER.error("User with ID {} not found!", userId);
            throw new IllegalArgumentException("User not found for ID: " + userId);
        }
        return userOptional.get();
    }

    @Override
    protected User getUser(Trainee entity) {
        return resolveUser(entity.getUserId());
    }
}
