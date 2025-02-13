package uz.gym.crm.service.abstr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.gym.crm.domain.*;
import uz.gym.crm.mapper.ProfileMapper;
import uz.gym.crm.repository.BaseRepository;
import uz.gym.crm.repository.TrainingRepository;
import uz.gym.crm.repository.UserRepository;
import uz.gym.crm.util.PasswordGenerator;
import uz.gym.crm.util.UsernameGenerator;

import java.util.List;

@Service
@Transactional
public abstract class AbstractProfileService<T> extends BaseServiceImpl<T, BaseRepository<T>> implements ProfileService<T> {
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final BaseRepository<T> baseRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProfileService.class);

    public AbstractProfileService(UserRepository userRepository, TrainingRepository trainingRepository, BaseRepository<T> baseRepository) {
        super(baseRepository, userRepository);
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
        this.baseRepository = baseRepository;
    }

    public void updateProfile(String username, T updatedEntity) {
        LOGGER.debug("Updating profile for username: {}", username);
        T existingEntity = findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found for username: " + username));
        ProfileMapper.updateFields(existingEntity, updatedEntity);
        baseRepository.save(existingEntity);
        LOGGER.info("Profile updated successfully for username: {}", username);
    }

    public void activate(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found for username: " + username));
        user.setIsActive(true);
        userRepository.save(user);
    }

    public void deactivate(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found for username: " + username));
        user.setIsActive(false);
        userRepository.save(user);
    }

    public void prepareUser(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            List<User> existingUsers = userRepository.findAll();
            String uniqueUsername = UsernameGenerator.generateUniqueUsername(user, existingUsers);
            user.setUsername(uniqueUsername);
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("");
        }

    }

    public String generatePassword(User user) {
        String password = "";
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            password = PasswordGenerator.generatePassword();
            user.setPassword(passwordEncoder.encode(password));
        }
        return password;
    }

    protected abstract User getUser(T entity);
}
