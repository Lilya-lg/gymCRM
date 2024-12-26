package uz.gym.crm.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DomainClassesTest {

        @Test
        void testTrainee() {
            Trainee trainee = new Trainee();
            trainee.setId(1L);
            trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
            trainee.setAddress("123 Test Street");

            assertEquals(1L, trainee.getId());
            assertEquals(LocalDate.of(1990, 1, 1), trainee.getDateOfBirth());
            assertEquals("123 Test Street", trainee.getAddress());
        }

        @Test
        void testTrainer() {
            Trainer trainer = new Trainer();
            trainer.setId(1L);
            trainer.setSpecialization("Yoga");

            assertEquals(1L, trainer.getId());
            assertEquals("Yoga", trainer.getSpecialization());
        }

        @Test
        void testTraining() {
            Trainee trainee = new Trainee();
            Trainer trainer = new Trainer();
            Training training = new Training();

            training.setId(1L);
            training.setTrainingName("Advanced Yoga");
            training.setTrainingType(TrainingType.YOGA);
            training.setTrainingDate(LocalDate.of(2024, 12, 31));
            training.setTrainingDuration(90);
            training.setTrainee(trainee);
            training.setTrainer(trainer);

            assertEquals(1L, training.getId());
            assertEquals("Advanced Yoga", training.getTrainingName());
            assertEquals(TrainingType.YOGA, training.getTrainingType());
            assertEquals(LocalDate.of(2024, 12, 31), training.getTrainingDate());
            assertEquals(90, training.getTrainingDuration());
            assertEquals(trainee, training.getTrainee());
            assertEquals(trainer, training.getTrainer());
        }

        @Test
        void testUser() {
            Trainer user = new Trainer();
            user.setId(1L);
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setUsername("johndoe");
            user.setPassword("password123");
            user.setActive(true);

            assertEquals(1L, user.getId());
            assertEquals("John", user.getFirstName());
            assertEquals("Doe", user.getLastName());
            assertEquals("johndoe", user.getUsername());
            assertEquals("password123", user.getPassword());
            assertTrue(user.isActive());
        }
    }

