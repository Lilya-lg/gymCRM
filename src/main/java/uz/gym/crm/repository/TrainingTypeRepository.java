package uz.gym.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import uz.gym.crm.domain.PredefinedTrainingType;
import uz.gym.crm.domain.TrainingType;

import java.util.Optional;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {

    Optional<TrainingType> findByTrainingType(PredefinedTrainingType trainingType);

    @Transactional
    default TrainingType getOrCreateTrainingType(PredefinedTrainingType predefinedType) {
        return findByTrainingType(predefinedType).orElseGet(() -> {
            TrainingType trainingType = new TrainingType();
            trainingType.setTrainingType(predefinedType);
            return save(trainingType);
        });
    }
}
