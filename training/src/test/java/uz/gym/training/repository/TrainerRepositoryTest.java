package uz.gym.training.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.gym.training.domain.Trainer;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrainerRepositoryTest {

    private TrainerRepository trainerRepository;

    @BeforeEach
    void setUp() {
        trainerRepository = new TrainerRepository();
    }

    @Test
    void testSaveAndFindById() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("JohnDoe");

        trainerRepository.save(trainer);
        Optional<Trainer> foundTrainer = trainerRepository.findById(1L);

        assertTrue(foundTrainer.isPresent());
        assertEquals("JohnDoe", foundTrainer.get().getUsername());
    }

    @Test
    void testFindByUsername() {
        Trainer trainer = new Trainer();
        trainer.setId(2L);
        trainer.setUsername("JaneDoe");

        trainerRepository.save(trainer);

        Optional<Trainer> foundTrainer = trainerRepository.findByUsername("JaneDoe");
        assertTrue(foundTrainer.isPresent());
        assertEquals(2L, foundTrainer.get().getId());

        // Check case-insensitivity and trimming
        assertTrue(trainerRepository.findByUsername("   janedoe   ").isPresent());
    }

    @Test
    void testFindAll() {
        Trainer trainer1 = new Trainer();
        trainer1.setId(3L);
        trainer1.setUsername("TrainerOne");

        Trainer trainer2 = new Trainer();
        trainer2.setId(4L);
        trainer2.setUsername("TrainerTwo");

        trainerRepository.save(trainer1);
        trainerRepository.save(trainer2);

        List<Trainer> allTrainers = trainerRepository.findAll();
        assertEquals(2, allTrainers.size());
    }

    @Test
    void testDeleteById() {
        Trainer trainer = new Trainer();
        trainer.setId(5L);
        trainer.setUsername("DeleteMe");

        trainerRepository.save(trainer);
        assertTrue(trainerRepository.findById(5L).isPresent());

        trainerRepository.deleteById(5L);
        assertFalse(trainerRepository.findById(5L).isPresent());
    }
}

