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
import uz.gym.crm.service.TraineeService;
import uz.gym.crm.service.TrainerService;
import uz.gym.crm.service.TrainingService;

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
        assertNotNull(context.getBean(TraineeService.class), "TraineeService bean should be initialized.");
        assertNotNull(context.getBean(TrainerService.class), "TrainerService bean should be initialized.");
        assertNotNull(context.getBean(TrainingService.class), "TrainingService bean should be initialized.");
        assertNotNull(context.getBean("trainerStorage"), "TrainerStorage bean should be initialized.");
        assertNotNull(context.getBean("traineeStorage"), "TraineeStorage bean should be initialized.");
        assertNotNull(context.getBean("trainingStorage"), "TrainingStorage bean should be initialized.");
    }

    @Test
    void verifyStorageInitialization() {
        Map<Integer, Trainer> trainerStorage = (Map<Integer, Trainer>) context.getBean("trainerStorage");
        Map<Integer, Trainee> traineeStorage = (Map<Integer, Trainee>) context.getBean("traineeStorage");
        Map<Integer, Training> trainingStorage = (Map<Integer, Training>) context.getBean("trainingStorage");

        assertFalse(trainerStorage.isEmpty(), "Trainer storage should be populated. Verify trainers.json file.");
        assertFalse(traineeStorage.isEmpty(), "Trainee storage should be populated. Verify trainees.json file.");
        assertFalse(trainingStorage.isEmpty(), "Training storage should be populated. Verify trainings.json file.");
    }

    @Test
    void verifyTrainerStorageContent() {
        Map<Integer, Trainer> trainerStorage = (Map<Integer, Trainer>) context.getBean("trainerStorage");
        assertTrue(trainerStorage.containsKey(1), "Trainer storage should contain a trainer with ID 1.");

        Trainer trainer = trainerStorage.get(1);
        assertNotNull(trainer, "Trainer with ID 1 should exist.");
        assertEquals("Yoga", trainer.getSpecialization(), "Trainer specialization should match the input data.");
        assertNotNull(trainer.getUserId(), "Trainer should have a User object.");
    }

    @Test
    void verifyTraineeStorageContent() {
        // Check specific data in traineeStorage
        @SuppressWarnings("unchecked")
        Map<Integer, Trainee> traineeStorage = (Map<Integer, Trainee>) context.getBean("traineeStorage");
        assertTrue(traineeStorage.containsKey(1), "Trainee storage should contain a trainee with ID 1.");

        Trainee trainee = traineeStorage.get(1);
        assertNotNull(trainee, "Trainee with ID 1 should exist.");
        assertNotNull(trainee.getUserId(), "Trainee should have a User object.");
    }

    @Test
    void verifyFacadeFunctionality() {
        // Validate that the facade integrates with the services correctly
        GymFacade gymFacade = context.getBean(GymFacade.class);

        assertNotNull(gymFacade.getTraineeService(), "GymFacade should have TraineeService injected.");
        assertNotNull(gymFacade.getTrainerService(), "GymFacade should have TrainerService injected.");
        assertNotNull(gymFacade.getTrainingService(), "GymFacade should have TrainingService injected.");

        // Test TraineeService call via the facade
        TraineeService traineeService = gymFacade.getTraineeService();
        assertNotNull(traineeService.getAll(), "TraineeService should return a non-null list of trainees.");
        assertFalse(traineeService.getAll().isEmpty(), "TraineeService should return a non-empty list if data exists.");

        // Test TrainerService call via the facade
        TrainerService trainerService = gymFacade.getTrainerService();
        assertNotNull(trainerService.getAll(), "TrainerService should return a non-null list of trainers.");
        assertFalse(trainerService.getAll().isEmpty(), "TrainerService should return a non-empty list if data exists.");
    }
}
