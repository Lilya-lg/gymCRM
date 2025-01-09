package uz.gym.crm.service.abstr;

import uz.gym.crm.domain.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProfileService<T> {
    void updateProfile(String username, String password,String usernameToUpdate, T updatedEntity);

    Optional<T> findByUsername(String username);

    boolean authenticate(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);

    void activate(String username,String usernameToActive,String password);

    void deactivate(String username,String usernameToDeactive, String password);
}
