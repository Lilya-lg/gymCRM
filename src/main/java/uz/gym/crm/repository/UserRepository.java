package uz.gym.crm.repository;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uz.gym.crm.domain.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndPassword(String username, String password);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u = :user WHERE u.id = :#{#user.id}")
    void updateUser(User user);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.username = :username")
    int updatePassword(@Param("username") String username, @Param("newPassword") String newPassword);
}
