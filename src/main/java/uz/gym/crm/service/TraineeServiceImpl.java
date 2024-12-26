package uz.gym.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.gym.crm.dao.TraineeDAOImpl;
import uz.gym.crm.dao.TrainingDAOImpl;
import uz.gym.crm.dao.UserDAOImpl;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.User;

import java.util.Optional;


@Service
public class TraineeServiceImpl extends AbstractProfileService<Trainee> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private final UserDAOImpl userDAO;
    private final TraineeDAOImpl traineeDAO;
    private final TrainingDAOImpl trainingDAO;


    public TraineeServiceImpl(UserDAOImpl userDAO, TraineeDAOImpl traineeDAO, TrainingDAOImpl trainingDAO) {
        super(traineeDAO, userDAO);
        this.userDAO = userDAO;
        this.traineeDAO = traineeDAO;
        this.trainingDAO = trainingDAO;
    }


    //1. Create Trainee profile
    @Override
    public void create(Trainee trainee) {
        prepareUser(trainee.getUser());
        super.create(trainee);
        LOGGER.info("Trainee entity created successfully with ID: {}", trainee.getId());
    }


    //13.Delete trainee profile
    public void deleteProfileByUsername(String username, String password) {
        LOGGER.debug("Deleting Trainee profile with username: {}", username);

        super.authenticate(username, password);

        Trainee trainee = traineeDAO.findByUser_Username(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found for username: " + username));

        User user = trainee.getUser();
        dao.delete(trainee.getId());
        //to check
        //userDAO.delete(user);

        LOGGER.info("Trainee profile and associated user deleted successfully for username: {}", username);
    }

    //5
    public Optional<Trainee> findByUsername(String username) {
        LOGGER.debug("Searching for profile with username: {}", username);
        return dao.findByUsername(username);
    }

    //3. Trainee username and password matching
    public Optional<Trainee> findByUsernameAndPassword(String username, String password) {
        LOGGER.debug("Attempting to find trainer with username: {} and password: {}", username, password);
        try {
            return traineeDAO.findByUser_UsernameAndUser_Password(username, password);
        } catch (Exception e) {
            LOGGER.error("Error finding trainer with username: {} and password: {}", username, password, e);
            throw e;
        }
    }
    //update Trainee's trainers list
    /*
    public void updateTraineeTrainers(String traineeUsername, List<Long> trainerIds) {
        LOGGER.debug("Updating trainers list for trainee with username: {}", traineeUsername);

        Trainee trainee = findByUsername(traineeUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found for username: " + traineeUsername));

        List<Trainer> trainers = trainerRepository.findAllById(trainerIds);
        if (trainers.size() != trainerIds.size()) {
            throw new IllegalArgumentException("One or more trainer IDs are invalid.");
        }

        List<Training> existingTrainings = trainingDAO.findByTraineeUsername(traineeUsername);

        existingTrainings.stream()
                .filter(training -> !trainerIds.contains(training.getTrainer().getId()))
                .forEach(training -> {
                    LOGGER.info("Removing outdated training: {}", training);
                    trainingDAO.delete(training.getId());
                });

        trainers.forEach(trainer -> {
            boolean exists = existingTrainings.stream()
                    .anyMatch(training -> training.getTrainer().getId().equals(trainer.getId()));

            if (!exists) {
                Training newTraining = new Training();
                newTraining.setTrainee(trainee);
                newTraining.setTrainer(trainer);
                newTraining.setTrainingName("Updated Training");
                newTraining.setTrainingDate(LocalDate.now());
                newTraining.setTrainingDuration(60);
                trainingDAO.save(newTraining);

                LOGGER.info("Created new training association: {}", newTraining);
            }
        });

        LOGGER.info("Trainers list updated successfully for trainee with username: {}", traineeUsername);
    }



     */


    @Override
    protected User getUser(Trainee entity) {
        return entity.getUser();
    }


}
