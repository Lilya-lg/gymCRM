package uz.gym.crm.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.gym.crm.dao.TraineeDAO;
import uz.gym.crm.dao.UserDAO;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.User;
import uz.gym.crm.util.PasswordGenerator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TraineeServiceImpl extends AbstractProfileService<Trainee, Integer> implements TraineeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private final UserDAO userDAO;
    public TraineeServiceImpl(TraineeDAO traineeDAO,UserDAO userDAO) {
        super(traineeDAO);
        this.userDAO = userDAO;
    }
    @Override
    public void create(Trainee trainee) {
        User user = resolveUser(trainee.getUserId());
        trainee.setUserId(trainee.getUserId());
        prepareUser(user);
        super.create(trainee);
    }
    private User resolveUser(Integer userId) {
        // Resolve User entity by userId
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
