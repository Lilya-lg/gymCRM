package uz.gym.crm.service.abstr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.gym.crm.dao.abstr.BaseDAO;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.*;
import uz.gym.crm.util.PasswordGenerator;
import uz.gym.crm.util.ProfileMapper;
import uz.gym.crm.util.UsernameGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public abstract class AbstractProfileService<T> extends BaseServiceImpl<T> implements ProfileService<T> {
    private UserDAOImpl userDAO;
    private TrainingDAOImpl trainingDAO;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProfileService.class);


    public AbstractProfileService(BaseDAO<T> dao, UserDAOImpl userDAO, TrainingDAOImpl trainingDAO) {
        super(dao, userDAO);
        this.userDAO = userDAO;
        this.trainingDAO = trainingDAO;
    }

    public void updateProfile(String username, String password, String usernameToUpdate, T updatedEntity) {
        LOGGER.debug("Updating profile for username: {}", usernameToUpdate);

        if (!authenticate(username, password)) {
            LOGGER.error("Authentication failed for username: {}", username);
            throw new IllegalArgumentException("Invalid username or password.");
        }

        T existingEntity = findByUsername(usernameToUpdate).orElseThrow(() -> new IllegalArgumentException("User not found for username: " + usernameToUpdate));

        ProfileMapper.updateFields(existingEntity, updatedEntity);


        dao.save(existingEntity);
        LOGGER.info("Profile updated successfully for username: {}", username);
    }


    public Optional<T> findByUsername(String username) {
        LOGGER.debug("Searching for profile with username: {}", username);
        return dao.findByUsername(username);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        if (!authenticate(username, oldPassword)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        User user = userDAO.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found for username: " + username));

        user.setPassword(newPassword);
        userDAO.save(user);
    }


    public void activate(String username, String usernameToActive, String password) {
        LOGGER.debug("Activating profile with username: {}", username);
        if (!authenticate(username, password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        T entity = findByUsername(usernameToActive).orElseThrow(() -> new IllegalArgumentException("Profile not found for username: " + username));

        User user = getUser(entity);
        user.setActive(true);

        dao.save(entity);
        LOGGER.info("Profile with username {} activated successfully.", username);
    }


    public void deactivate(String username, String usernameToDeactive, String password) {
        LOGGER.debug("Deactivating profile with username: {}", username);
        if (!authenticate(username, password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        T entity = findByUsername(usernameToDeactive).orElseThrow(() -> new IllegalArgumentException("Profile not found for username: " + username));

        User user = getUser(entity);
        user.setActive(false);

        dao.save(entity);
        LOGGER.info("Profile with username {} deactivated successfully.", username);
    }


    public List<Training> getTrainingListByCriteria(String username, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingType, String traineeName, String usernameAuth, String password) {
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

    protected abstract User getUser(T entity);


}

