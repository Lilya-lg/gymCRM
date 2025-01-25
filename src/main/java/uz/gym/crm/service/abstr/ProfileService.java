package uz.gym.crm.service.abstr;
import java.util.Optional;

public interface ProfileService<T> {
    void updateProfile(String username, T updatedEntity);

    Optional<T> findByUsername(String username);

    boolean authenticate(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);

    void activate(String username);

    void deactivate(String username);
}
