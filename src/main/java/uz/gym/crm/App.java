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

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        GymFacade facade = context.getBean(GymFacade.class);

        LOGGER.info("GymFacade initialized successfully.");
        logStorageBeanSizes(context);

        LOGGER.info("Gym CRM application setup completed.");

        createAndLogTrainee(facade);

        createAndLogTrainer(facade);

        LOGGER.info("Gym CRM application terminated successfully.");
    }

    private static void logStorageBeanSizes(ApplicationContext context) {
        LOGGER.info("Trainer storage size: {}", context.getBean("trainerStorage", java.util.Map.class).size());
        LOGGER.info("Trainee storage size: {}", context.getBean("traineeStorage", java.util.Map.class).size());
        LOGGER.info("Training storage size: {}", context.getBean("trainingStorage", java.util.Map.class).size());
        LOGGER.info("User storage size: {}", context.getBean("userStorage", java.util.Map.class).size());
    }

    private static void createAndLogTrainee(GymFacade facade) {
        LOGGER.info("Creating a new trainee...");
        Trainee newTrainee = new Trainee();
        newTrainee.setId(1L);
        newTrainee.setFirstName("Jane");
        newTrainee.setLastName("Smith");

        facade.getTraineeService().create(newTrainee);

        LOGGER.info("Fetching all trainees after creation:");
        facade.getTraineeService().getAll().forEach(trainee -> {
            LOGGER.info("Trainee: ID={}, FirstName={}, LastName={}, Username={}",
                    trainee.getId(), trainee.getFirstName(), trainee.getLastName(), trainee.getUsername());
        });
    }

    private static void createAndLogTrainer(GymFacade facade) {
        LOGGER.info("Creating a new trainer...");
        Trainer newTrainer = new Trainer();
        newTrainer.setId(1L); // Unique ID for the trainer
        newTrainer.setFirstName("John");
        newTrainer.setLastName("Doe");
        newTrainer.setSpecialization("Yoga");

        facade.getTrainerService().create(newTrainer);

        LOGGER.info("Fetching all trainers after creation:");
        facade.getTrainerService().getAll().forEach(trainer -> {
            LOGGER.info("Trainer: ID={}, FirstName={}, LastName={}, Username={}, Specialization={}",
                    trainer.getId(), trainer.getFirstName(), trainer.getLastName(), trainer.getUsername(), trainer.getSpecialization());
        });
    }
}
