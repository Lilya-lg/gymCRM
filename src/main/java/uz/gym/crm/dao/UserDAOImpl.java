package uz.gym.crm.dao;

import org.springframework.stereotype.Repository;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;

import java.util.Map;
import java.util.Optional;

@Repository
public class UserDAOImpl extends BaseDAOImpl<User, Long> {

    public UserDAOImpl(Map<Long, User> storage) {
        super(storage, User::getId);
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<User> findByUsername(String username) {
        return storage.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }
}

