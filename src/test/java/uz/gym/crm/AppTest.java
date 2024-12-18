package uz.gym.crm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import uz.gym.crm.config.AppConfig;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;
import uz.gym.crm.facade.GymFacade;
import uz.gym.crm.service.TraineeServiceImpl;
import uz.gym.crm.service.TrainerServiceImpl;
import uz.gym.crm.service.TrainingServiceImpl;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    private static ApplicationContext context;

    @BeforeAll
    static void initContext() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
    }

    @Test
    void contextLoads() {
        assertNotNull(context, "Application context should not be null.");
    }

    @Test
    void verifyBeansInitialization() {
        assertNotNull(context.getBean(GymFacade.class), "GymFacade bean should be initialized.");
        assertNotNull(context.getBean(TraineeServiceImpl.class), "TraineeService bean should be initialized.");
        assertNotNull(context.getBean(TrainerServiceImpl.class), "TrainerService bean should be initialized.");
        assertNotNull(context.getBean(TrainingServiceImpl.class), "TrainingService bean should be initialized.");
        assertNotNull(context.getBean("trainerStorage"), "TrainerStorage bean should be initialized.");
        assertNotNull(context.getBean("traineeStorage"), "TraineeStorage bean should be initialized.");
        assertNotNull(context.getBean("trainingStorage"), "TrainingStorage bean should be initialized.");
    }

    @Test
    void verifyStorageInitialization() {
        Map<Long, Trainer> trainerStorage = (Map<Long, Trainer>) context.getBean("trainerStorage");
        Map<Long, Trainee> traineeStorage = (Map<Long, Trainee>) context.getBean("traineeStorage");
        Map<Long, Training> trainingStorage = (Map<Long, Training>) context.getBean("trainingStorage");

        assertFalse(trainerStorage.isEmpty(), "Trainer storage should be populated. Verify trainers.json file.");
        assertFalse(traineeStorage.isEmpty(), "Trainee storage should be populated. Verify trainees.json file.");
        assertFalse(trainingStorage.isEmpty(), "Training storage should be populated. Verify trainings.json file.");
    }

    @Test
    void verifyTrainerStorageContent() {
        Map<Long, Trainer> trainerStorage = (Map<Long, Trainer>) context.getBean("trainerStorage");
        assertTrue(trainerStorage.containsKey(1L), "Trainer storage should contain a trainer with ID 1.");

        Trainer trainer = trainerStorage.get(1L);
        assertNotNull(trainer, "Trainer with ID 1 should exist.");
        assertEquals("Yoga", trainer.getSpecialization(), "Trainer specialization should match the input data.");
        assertNotNull(trainer.getUserId(), "Trainer should have a User object.");
    }

    @Test
    void verifyTraineeStorageContent() {
        Map<Long, Trainee> traineeStorage = (Map<Long, Trainee>) context.getBean("traineeStorage");
        assertTrue(traineeStorage.containsKey(1L), "Trainee storage should contain a trainee with ID 1.");

        Trainee trainee = traineeStorage.get(1L);
        assertNotNull(trainee, "Trainee with ID 1 should exist.");
        assertNotNull(trainee.getUserId(), "Trainee should have a User object.");
    }

}
