package uz.gym.crm.dao;

import uz.gym.crm.domain.User;

import java.util.Optional;

public interface UserDAO extends BaseDAO<User, Integer> {
    // Finds a user by their ID
    Optional<User> findById(Integer id);

    // Finds a user by username
    Optional<User> findByUsername(String username);
}
