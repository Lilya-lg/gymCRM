package uz.gym.crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import uz.gym.crm.domain.PredefinedTrainingType;
import uz.gym.crm.domain.TrainingType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class TrainingTypeRepositoryTest {

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Test
    void testFindByTrainingType() {

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingType(PredefinedTrainingType.PILATES);
        trainingTypeRepository.save(trainingType);


        Optional<TrainingType> foundTrainingType = trainingTypeRepository.findByTrainingType(PredefinedTrainingType.PILATES);
        assertTrue(foundTrainingType.isPresent());
        assertEquals(PredefinedTrainingType.PILATES, foundTrainingType.get().getTrainingType());
    }

    @Test
    void testGetOrCreateTrainingType() {

        TrainingType trainingType = trainingTypeRepository.getOrCreateTrainingType(PredefinedTrainingType.YOGA);
        assertNotNull(trainingType);
        assertEquals(PredefinedTrainingType.YOGA, trainingType.getTrainingType());
    }
}