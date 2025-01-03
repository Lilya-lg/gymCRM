package uz.gym.crm.dao;

import uz.gym.crm.domain.User;

import java.util.Optional;

public interface UserDAO extends BaseDAO<User> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndPassword(String username, String password);
}