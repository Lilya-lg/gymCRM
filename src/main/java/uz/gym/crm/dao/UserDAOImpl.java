package uz.gym.crm.dao;

import org.springframework.stereotype.Repository;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;

import java.util.Map;
import java.util.Optional;

@Repository
public class UserDAOImpl extends BaseDAOImpl<User> {

    public UserDAOImpl(Map<Long, User> storage) {
        super(storage, User::getId);
    }
}

