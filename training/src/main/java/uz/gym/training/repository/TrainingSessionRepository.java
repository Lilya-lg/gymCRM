package uz.gym.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.gym.training.domain.Trainer;
import uz.gym.training.domain.TrainingSession;

import java.util.List;


public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
    List<TrainingSession> findByTrainer(Trainer trainer);
}
