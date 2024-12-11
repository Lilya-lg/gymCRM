package uz.gym.crm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import uz.gym.crm.config.AppConfig;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;
import uz.gym.crm.facade.GymFacade;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) {
        LOGGER.info("Starting Gym CRM application...");
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        GymFacade facade = context.getBean(GymFacade.class);
        LOGGER.info("GymFacade initialized: {}", facade);

        LOGGER.info("Trainer storage: {}", context.getBean("trainerStorage"));
        LOGGER.info("Trainee storage: {}", context.getBean("traineeStorage"));
        LOGGER.info("Training storage: {}", context.getBean("trainingStorage"));
        LOGGER.info("User storage: {}", context.getBean("userStorage"));

        LOGGER.info("Gym CRM application started successfully.");
        GymFacade gymFacade = context.getBean(GymFacade.class);
        // Test Trainee Service
        LOGGER.info("Fetching all trainees:");
        gymFacade.getTraineeService().getAll().forEach(System.out::println);

        Trainee newTrainee = new Trainee();
        newTrainee.setId(1);
        newTrainee.setUserId(2); // Refers to User with ID 2
        facade.getTraineeService().create(newTrainee);

        LOGGER.info("Fetching all trainees:");
        facade.getTraineeService().getAll().forEach(trainee -> {
            LOGGER.info("Trainee: " + trainee);
            int userId = trainee.getUserId();
            LOGGER.info("Associated User: " + userId);
        });

        // Create and check Trainer
        Trainer newTrainer = new Trainer();
        newTrainer.setId(1);
        newTrainer.setSpecialization("Yoga");
        newTrainer.setUserId(1); // Refers to User with ID 1
        facade.getTrainerService().create(newTrainer);

        LOGGER.info("Fetching all trainers:");
        facade.getTrainerService().getAll().forEach(trainer -> {
            LOGGER.info("Trainer: " + trainer.getSpecialization());
            int userId = trainer.getUserId();
            LOGGER.info("Associated User: " + userId);
        });

        LOGGER.info("Gym CRM application completed successfully.");
    }
}
