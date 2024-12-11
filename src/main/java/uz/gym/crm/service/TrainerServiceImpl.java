package uz.gym.crm.service;


import org.springframework.stereotype.Service;
import uz.gym.crm.dao.TrainerDAO;
import uz.gym.crm.dao.UserDAO;
import uz.gym.crm.domain.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.gym.crm.domain.User;
import uz.gym.crm.util.PasswordGenerator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainerServiceImpl extends AbstractProfileService<Trainer, Integer> implements TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private final UserDAO userDAO;
    public TrainerServiceImpl(TrainerDAO trainerDAO,UserDAO userDAO) {
        super(trainerDAO);
        this.userDAO = userDAO;
    }
    @Override
    public void create(Trainer trainer) {
        User user = resolveUser(trainer.getUserId());
        trainer.setUserId(user.getId());
        prepareUser(user);
        super.create(trainer);
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
    protected User getUser(Trainer entity) {
        return resolveUser(entity.getUserId());
    }
}
