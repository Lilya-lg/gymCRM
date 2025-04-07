package uz.micro.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uz.micro.gym.domain.PredefinedTrainingType;
import uz.micro.gym.domain.Trainee;
import uz.micro.gym.domain.Trainer;
import uz.micro.gym.domain.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("SELECT t FROM Training t JOIN t.trainee trainee JOIN t.trainer trainer " + "JOIN trainee.user traineeUser JOIN trainer.user trainerUser " + "WHERE traineeUser.username = :traineeUsername " + "AND (:trainingType IS NULL OR t.trainingType.trainingType = :trainingType) " + "AND (CAST(:fromDate AS date) IS NULL OR t.trainingDate >= :fromDate) " + "AND (CAST(:toDate AS date) IS NULL OR t.trainingDate <= :toDate) " + "AND (:trainerName IS NULL OR trainerUser.username = :trainerName)")
    List<Training> findByCriteria(@Param("traineeUsername") String traineeUsername, @Param("trainingType") PredefinedTrainingType trainingType, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, @Param("trainerName") String trainerName);

    @Query("SELECT t FROM Training t JOIN t.trainer trainer JOIN t.trainee trainee " + "JOIN trainer.user trainerUser JOIN trainee.user traineeUser " + "WHERE trainerUser.username = :trainerUsername " + "AND (CAST(:fromDate AS date) IS NULL OR t.trainingDate >= :fromDate) " + "AND (CAST(:toDate AS date) IS NULL OR t.trainingDate <= :toDate) " + "AND (:traineeName IS NULL OR traineeUser.username = :traineeName)")
    List<Training> findByCriteriaForTrainer(@Param("trainerUsername") String trainerUsername, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, @Param("traineeName") String traineeName);

    @Query("SELECT DISTINCT t.trainer FROM Training t WHERE t.trainee.id = :traineeId")
    List<Trainer> findTrainersByTraineeId(Long traineeId);

    @Query("SELECT DISTINCT t.trainee FROM Training t WHERE t.trainer.id = :trainerId")
    List<Trainee> findTraineesByTrainerId(Long trainerId);

    List<Training> findByTraineeId(Long traineeId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Training t WHERE t.id = :id")
    void deleteById(@Param("id") Long id);
}
