package uz.gym.crm.dao;
import org.springframework.stereotype.Repository;
import uz.gym.crm.dao.UserDAO;
import uz.gym.crm.domain.Training;
import uz.gym.crm.domain.User;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Repository
public class UserDAOImpl extends BaseDAOImpl<User, Integer> implements UserDAO {

    public UserDAOImpl(Map<Integer, User> userStorage) {
        super(User::getId);
        this.storage.putAll(userStorage);
    }
    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return storage.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }
}

