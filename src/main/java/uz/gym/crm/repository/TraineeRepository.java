package uz.gym.crm.repository;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import uz.gym.crm.domain.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository extends BaseRepository<Trainee> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Trainee t WHERE t.user.username = :username")
    void deleteByUsername(String username);

    @Query("SELECT t FROM Trainee t WHERE t.user.username = :username")
    Optional<Trainee> findByUsername(String username);
    @Transactional
    @Modifying
    @Query(value = """
        UPDATE trainings 
        SET trainer_id = :trainerId 
        WHERE trainee_id = :traineeId AND id = :trainingId
    """, nativeQuery = true)
    void updateTraineeTrainer(Long traineeId, Long trainingId, Long trainerId);
}
