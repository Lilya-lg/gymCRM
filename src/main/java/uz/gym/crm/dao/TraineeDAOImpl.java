package uz.gym.crm.dao;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public class TraineeDAOImpl extends BaseDAOImpl<Trainee> implements TraineeDAO {
    private final Session session;
    private static final String FIND_BY_USERNAME_AND_PASSWORD = "SELECT t FROM Trainee t JOIN t.user u WHERE u.username = :username AND u.password = :password";
    private static final String FIND_BY_USERNAME = "SELECT t FROM Trainee t JOIN t.user u WHERE u.username = :username";
    private static final String FETCH_TRAININGS_BY_TRAINEE = "SELECT t FROM Training t WHERE t.trainee.id = :traineeId";

    public TraineeDAOImpl(Session session) {
        super(Trainee.class, session);
        this.session = session;

    }

    public Optional<Trainee> findByUsernameAndPassword(String username, String password) {
        Trainee trainee = session.createQuery(FIND_BY_USERNAME_AND_PASSWORD, Trainee.class).setParameter("username", username).setParameter("password", password).uniqueResult();
        return Optional.ofNullable(trainee);
    }

    public Optional<Trainee> findByUsername(String username) {
        Trainee result = session.createQuery(FIND_BY_USERNAME, Trainee.class).setParameter("username", username).uniqueResult();
        return Optional.ofNullable(result);
    }

    @Transactional
    public void updateTraineeTrainerList(Long traineeId, List<Long> trainerIds) {
        Trainee trainee = session.find(Trainee.class, traineeId);
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee with ID " + traineeId + " not found");
        }


        List<Training> existingTrainings = session.createQuery(FETCH_TRAININGS_BY_TRAINEE, Training.class).setParameter("traineeId", traineeId).list();


        for (Training training : existingTrainings) {
            if (!trainerIds.contains(training.getTrainer().getId())) {
                session.remove(training);
            }
        }


        for (Long trainerId : trainerIds) {
            boolean exists = existingTrainings.stream().anyMatch(training -> training.getTrainer().getId().equals(trainerId));

            if (!exists) {

                Trainer trainer = session.find(Trainer.class, trainerId);
                if (trainer == null) {
                    throw new IllegalArgumentException("Trainer with ID " + trainerId + " not found");
                }


                Training newTraining = new Training();
                newTraining.setTrainee(trainee);
                newTraining.setTrainer(trainer);
                newTraining.setTrainingName("Yoga");
                newTraining.setTrainingDate(LocalDate.now());
                newTraining.setTrainingDuration(60);
                newTraining.setTrainingType(trainer.getSpecialization());

                session.persist(newTraining);
            }
        }
    }

}
