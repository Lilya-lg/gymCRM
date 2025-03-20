package uz.micro.gym.repository;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uz.micro.gym.domain.Trainee;

import java.util.Optional;

public interface TraineeRepository extends BaseRepository<Trainee> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Trainee t WHERE t.user.username = :username")
    int deleteByUsername(@Param("username") String username);

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
