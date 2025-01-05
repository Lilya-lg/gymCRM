package uz.gym.crm.service;

import uz.gym.crm.domain.Training;
import uz.gym.crm.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProfileService<T> {
    void updateProfile(String username, String password, T updatedEntity);

    Optional<T> findByUsername(String username);

    boolean authenticate(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);

    void activate(String username);

    void deactivate(String username, String password);

    List<Training> getTrainingListByCriteria(String username, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingType);
}
