package uz.gym.crm.service;

import org.springframework.stereotype.Service;
import uz.gym.crm.dao.BaseDAO;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.gym.crm.domain.User;

import java.util.Optional;

@Service
public class TrainerServiceImpl extends AbstractProfileService<Trainer> implements BaseService<Trainer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private final BaseDAO<User> userDAO;
    public TrainerServiceImpl(BaseDAO<Trainer> trainerDAO,BaseDAO<User> userDAO) {
        super(trainerDAO,userDAO);
        this.userDAO = userDAO;
    }

    @Override
    public void create(Trainer trainer) {
        prepareUser(trainer);
        super.create(trainer);
        LOGGER.info("Trainer entity created successfully with ID: {}", trainer.getId());
    }
    @Override
    protected User getUser(Trainer entity) {
        return userDAO.read(entity.getId())
                .orElseThrow(() -> {
                    LOGGER.error("User with ID {} not found!", entity.getId());
                    return new IllegalArgumentException("User not found for ID: " + entity.getId());
                });
    }


}
