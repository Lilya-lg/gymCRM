package uz.gym.crm.service;

import org.springframework.stereotype.Service;
import uz.gym.crm.dao.BaseDAO;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.gym.crm.domain.User;

import java.util.Optional;

@Service
public class TrainerServiceImpl extends AbstractProfileService<Trainer> implements BaseService<Trainer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private final UserDAOImpl userDAO;

    public TrainerServiceImpl(BaseDAO<Trainer> trainerDAO, UserDAOImpl userDAO) {
        super(trainerDAO, userDAO);
        this.userDAO = userDAO;
    }

    @Override
    public void create(Trainer trainer) {
        Long userId = trainer.getUserId();
        User user = resolveUser(userId);
        prepareUser(user);
        if (user.getId() == 0) {
            userDAO.create(user);
        }
        trainer.setUserId(user.getId());
        super.create(trainer);
        LOGGER.info("Trainer entity created successfully with ID: {}", trainer.getId());
    }

    @Override
    public User getUser(Trainer entity) {
        return resolveUser(entity.getUserId());
    }

    private User resolveUser(Long userId) {
        return userDAO.read(userId).orElseThrow(() -> {
            LOGGER.error("User with ID {} not found! Proceeding without user.", userId);
            return new IllegalArgumentException("User not found for ID: " + userId);
        });
    }
}
