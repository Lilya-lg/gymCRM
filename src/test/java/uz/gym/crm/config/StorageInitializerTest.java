package uz.gym.crm.config;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import uz.gym.crm.init.StorageInitializer;
import uz.gym.crm.domain.Trainer;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

class StorageInitializerTest {

    @Test
    void testTrainerStorageInitialization() throws Exception {
        StorageInitializer initializer = new StorageInitializer();
        ClassPathResource trainerFile = new ClassPathResource("trainers.json");

        Map<Long, Trainer> trainerStorage = new HashMap<>();
        initializer.populateStorage(trainerStorage, trainerFile, Trainer.class);

        assertFalse(trainerStorage.isEmpty(), "Trainer storage should not be empty after initialization.");
    }
}


