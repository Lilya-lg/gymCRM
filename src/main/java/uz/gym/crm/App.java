package uz.gym.crm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import uz.gym.crm.config.AppConfig;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.facade.GymFacade;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOGGER.info("Starting Gym CRM application...");

        // Initialize Spring context
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        GymFacade facade = context.getBean(GymFacade.class);

        LOGGER.info("GymFacade initialized: {}", facade);

        // Log storage beans
        LOGGER.info("Trainer storage: {}", context.getBean("trainerStorage"));
        LOGGER.info("Trainee storage: {}", context.getBean("traineeStorage"));
        LOGGER.info("Training storage: {}", context.getBean("trainingStorage"));
        LOGGER.info("User storage: {}", context.getBean("userStorage"));

        LOGGER.info("Gym CRM application started successfully.");

        // === Test Trainee Creation ===
        LOGGER.info("Creating a new trainee...");
        Trainee newTrainee = new Trainee();
        newTrainee.setId(1L); // Unique ID for the trainee
        newTrainee.setUserId(2L); // References User ID to be resolved internally
        facade.getTraineeService().create(newTrainee);

        LOGGER.info("Fetching all trainees after creation:");
        facade.getTraineeService().getAll().forEach(trainee -> {
            LOGGER.info("Trainee ID: {}", trainee.getId());
            LOGGER.info("Associated User ID: {}", trainee.getUserId());
        });

        // === Test Trainer Creation ===
        LOGGER.info("Creating a new trainer...");
        Trainer newTrainer = new Trainer();
        newTrainer.setId(1L); // Unique ID for the trainer
        newTrainer.setSpecialization("Yoga");
        newTrainer.setUserId(1L); // References User ID to be resolved internally
        facade.getTrainerService().create(newTrainer);

        LOGGER.info("Fetching all trainers after creation:");
        facade.getTrainerService().getAll().forEach(trainer -> {
            LOGGER.info("Trainer ID: {}", trainer.getId());
            LOGGER.info("Specialization: {}", trainer.getSpecialization());
            LOGGER.info("Associated User ID: {}", trainer.getUserId());
        });

        LOGGER.info("Gym CRM application completed successfully.");
    }
}
