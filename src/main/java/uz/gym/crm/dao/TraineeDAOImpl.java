package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;

import java.util.Optional;
import java.util.Set;

@Repository
public class TraineeDAOImpl extends BaseDAOImpl<Trainee> implements TraineeDAO {
    private final Session session;
    private static final String FIND_BY_USERNAME_AND_PASSWORD =
            "SELECT t FROM Trainee t JOIN t.user u WHERE u.username = :username AND u.password = :password";
    private static final String FIND_BY_USERNAME =
            "SELECT t FROM Trainee t JOIN t.user u WHERE u.username = :username";


    public TraineeDAOImpl(Session session) {
        super(Trainee.class, session);
        this.session = session;

    }

    public Optional<Trainee> findByUsernameAndPassword(String username, String password) {
        Trainee trainee = session.createQuery(FIND_BY_USERNAME_AND_PASSWORD, Trainee.class)
                .setParameter("username", username)
                .setParameter("password", password)
                .uniqueResult();
        return Optional.ofNullable(trainee);
    }

    public Optional<Trainee> findByUsername(String username) {
        Trainee result = session.createQuery(FIND_BY_USERNAME, Trainee.class)
                .setParameter("username", username)
                .uniqueResult();
        return Optional.ofNullable(result);
    }


    public void updateTraineeTrainerList(Long traineeId, Set<Long> newTrainerIds) {
        Transaction transaction = session.beginTransaction();

        try {
            Trainee trainee = session.find(Trainee.class, traineeId);
            if (trainee == null) {
                throw new IllegalArgumentException("Trainee with ID " + traineeId + " not found");
            }

            for (Trainer trainer : trainee.getTrainers()) {
                trainer.getTrainees().remove(trainee);
            }
            trainee.getTrainers().clear();

            for (Long trainerId : newTrainerIds) {
                Trainer trainer = session.find(Trainer.class, trainerId);
                if (trainer == null) {
                    throw new IllegalArgumentException("Trainer with ID " + trainerId + " not found");
                }
                trainee.getTrainers().add(trainer);
                trainer.getTrainees().add(trainee);
            }


            session.update(trainee);

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }


}
