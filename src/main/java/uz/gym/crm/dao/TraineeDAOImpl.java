package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import uz.gym.crm.config.HibernateUtil;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class TraineeDAOImpl extends BaseDAOImpl<Trainee> {
    private final Session session;
    public TraineeDAOImpl(Session session) {
        super(Trainee.class,session);
        this.session = session;

    }
    public Optional<Trainee> findByUser_UsernameAndUser_Password(String username, String password) {

            String query = "SELECT t FROM Trainee t JOIN t.user u " +
                    "WHERE u.username = :username AND u.password = :password";
            Trainee trainee = session.createQuery(query, Trainee.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .uniqueResult();
            return Optional.ofNullable(trainee);

    }
    public Optional<Trainee> findByUser_Username(String username) {

            String query = "SELECT t FROM Trainee t JOIN t.user u WHERE u.username = :username";
            Trainee trainee = session.createQuery(query, Trainee.class)
                    .setParameter("username", username)
                    .uniqueResult();
            return Optional.ofNullable(trainee);

    }
    @Override
    public Optional<Trainee> findByUsername(String username) {

            String query = "SELECT t FROM Trainee t JOIN t.user u WHERE u.username = :username";
            Trainee result = session.createQuery(query, Trainee.class)
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

            // Clear current trainers
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
                trainer.getTrainees().add(trainee); // Synchronize the other side
            }


            session.update(trainee);

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }



}
