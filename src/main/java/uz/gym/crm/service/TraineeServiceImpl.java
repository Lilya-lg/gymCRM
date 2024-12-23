package uz.gym.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.gym.crm.dao.BaseDAO;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.User;


@Service
public class TraineeServiceImpl extends AbstractProfileService<Trainee> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private final BaseDAO<User> userDAO;

    public TraineeServiceImpl(BaseDAO<Trainee> traineeDAO, BaseDAO<User> userDAO) {
        super(traineeDAO, userDAO);
        this.userDAO = userDAO;
    }


    @Override
    public void create(Trainee trainee) {
        prepareUser(trainee);
        super.create(trainee);
        LOGGER.info("Trainee entity created successfully with ID: {}", trainee.getId());
    }

    @Override
    protected User getUser(Trainee entity) {
        return userDAO.read(entity.getId())
                .orElseThrow(() -> {
                    LOGGER.error("User with ID {} not found!", entity.getId());
                    return new IllegalArgumentException("User not found for ID: " + entity.getId());
                });
    }
}
