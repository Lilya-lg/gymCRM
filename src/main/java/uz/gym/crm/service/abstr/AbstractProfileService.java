package uz.gym.crm.service.abstr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.gym.crm.dao.abstr.BaseDAO;
import uz.gym.crm.dao.abstr.TrainingDAO;
import uz.gym.crm.dao.abstr.UserDAO;
import uz.gym.crm.domain.*;
import uz.gym.crm.util.PasswordGenerator;
import uz.gym.crm.util.ProfileMapper;
import uz.gym.crm.util.UsernameGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public abstract class AbstractProfileService<T> extends BaseServiceImpl<T> implements ProfileService<T> {
    private UserDAO userDAO;
    private TrainingDAO trainingDAO;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProfileService.class);


    public AbstractProfileService(BaseDAO<T> dao, UserDAO userDAO, TrainingDAO trainingDAO) {
        super(dao, userDAO);
        this.userDAO = userDAO;
        this.trainingDAO = trainingDAO;
    }

    public void updateProfile(String username, T updatedEntity) {
        LOGGER.debug("Updating profile for username: {}", username);
        T existingEntity = findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found for username: " + username));
        ProfileMapper.updateFields(existingEntity, updatedEntity);
        getDao().update(existingEntity);
        LOGGER.info("Profile updated successfully for username: {}", username);
    }


    public Optional<T> findByUsername(String username) {
        LOGGER.debug("Searching for profile with username: {}", username);
        return getDao().findByUsername(username);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userDAO.findByUsernameAndPassword(username,oldPassword).orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));
        user.setPassword(newPassword);
        userDAO.updateUser(user);
    }


    public void activate(String username) {
        LOGGER.debug("Activating profile with username: {}", username);
        User user = userDAO.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found for username: " + username));
        user.setActive(true);
        try {
            userDAO.updateUser(user);
        } catch (Exception e) {
            LOGGER.error("Can not update user",e);
        }
        LOGGER.info("Profile with username {} activated successfully.", username);
    }


    public void deactivate(String usernameToDeactive) {
        LOGGER.debug("Deactivating  profile with username: {}", usernameToDeactive);

            User user = userDAO.findByUsername(usernameToDeactive).orElseThrow(() -> new IllegalArgumentException("User not found for username: " + usernameToDeactive));
            user.setActive(false);
            userDAO.updateUser(user);
        LOGGER.info("Profile with username {} deactivated successfully.", usernameToDeactive);
    }


    public List<Training> getTrainingListByCriteria(String username, LocalDate fromDate, LocalDate toDate, String trainerName, PredefinedTrainingType trainingType, String traineeName, String usernameAuth, String password) {
        LOGGER.debug("Fetching training list with ORM filters for profile with username: {}", username);
        if (!authenticate(usernameAuth, password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        T profile = findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Profile not found for username: " + username));

        if (profile instanceof Trainee trainee) {
            return trainingDAO.findByCriteria(username, trainingType, fromDate, toDate, trainerName);
        } else if (profile instanceof Trainer trainer) {
            return trainingDAO.findByCriteriaForTrainer(trainerName, fromDate, toDate, traineeName);
        } else {
            throw new IllegalArgumentException("Unsupported profile type for username: " + username);
        }
    }

    public void prepareUser(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            List<User> existingUsers = userDAO.getAll();
            String uniqueUsername = UsernameGenerator.generateUniqueUsername(user, existingUsers);
            user.setUsername(uniqueUsername);
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(PasswordGenerator.generatePassword());
        }
    }

    @Override
    public boolean authenticate(String username, String password) {
        return userDAO.findByUsernameAndPassword(username, password).isPresent();
    }

    protected abstract User getUser(T entity);


}

