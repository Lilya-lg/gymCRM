package uz.micro.gym.repository;

import org.springframework.data.jpa.repository.Query;
import uz.micro.gym.domain.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends BaseRepository<Trainer> {
    @Query("SELECT t FROM Trainer t WHERE t.id NOT IN (SELECT tr.trainer.id FROM Training tr WHERE tr.trainee.user.username = :username)")
    List<Trainer> getUnassignedTrainersByTraineeUsername(String username);

    @Query("SELECT t FROM Trainer t WHERE t.user.username = :username")
    Optional<Trainer> findByUsername(String username);

    @Query("SELECT t FROM Trainer t WHERE t.user.username IN :usernames")
    List<Trainer> findByUsernameIn(List<String> usernames);
}
