package uz.gym.crm.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.User;
import uz.gym.crm.repository.BaseRepository;
import uz.gym.crm.repository.TrainingRepository;
import uz.gym.crm.repository.UserRepository;
import uz.gym.crm.service.abstr.AbstractProfileService;
import uz.gym.crm.service.abstr.UserService;

import java.util.Optional;


@Service
@Transactional
public class UserServiceImpl extends AbstractProfileService<User> implements UserService {
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final BaseRepository<User> baseRepository;

    public UserServiceImpl(UserRepository userRepository, TrainingRepository trainingRepository, BaseRepository<User> baseRepository) {
        super(userRepository, trainingRepository, baseRepository);
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
        this.baseRepository = baseRepository;
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        int updatedRows = userRepository.updatePassword(username, oldPassword, newPassword);
        if (updatedRows == 0) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
    }


    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }


    public boolean authenticate(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password).isPresent();
    }

    public void activate(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + username));
        user.setIsActive(true);
        userRepository.save(user);
    }

    @Override
    public void deactivate(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + username));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    protected User getUser(User entity) {
        return entity;
    }
}
