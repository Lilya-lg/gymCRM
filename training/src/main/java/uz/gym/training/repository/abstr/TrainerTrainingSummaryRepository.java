package uz.gym.training.repository.abstr;

import org.springframework.data.mongodb.repository.MongoRepository;
import uz.gym.training.domain.TrainerTrainingSummary;

import java.util.Optional;

public interface TrainerTrainingSummaryRepository extends MongoRepository<TrainerTrainingSummary, String> {
    Optional<TrainerTrainingSummary> findByTrainerUsername(String trainerUsername);
}