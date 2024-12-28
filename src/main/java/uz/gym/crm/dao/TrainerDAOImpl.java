package uz.gym.crm.dao;


import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import uz.gym.crm.config.HibernateUtil;
import uz.gym.crm.domain.Trainer;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDAOImpl extends BaseDAOImpl<Trainer> {
    private final Session session;
    public TrainerDAOImpl(Session session) {
        super(Trainer.class,session);
        this.session = session;
    }
    public Optional<Trainer> findByUser_UsernameAndUser_Password(String username, String password) {

            String query = "SELECT t FROM Trainer t JOIN t.user u " +
                    "WHERE u.username = :username AND u.password = :password";
            Trainer trainer = session.createQuery(query, Trainer.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .uniqueResult();
            return Optional.ofNullable(trainer);

    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
            String query = "SELECT t FROM Trainer t JOIN t.user u WHERE u.username = :username";
            Trainer result = session.createQuery(query, Trainer.class)
                    .setParameter("username", username)
                    .uniqueResult();
            return Optional.ofNullable(result);
    }
    public List<Trainer> getUnassignedTrainersByTraineeUsername(String traineeUsername) {
            String sql = """
        SELECT * FROM trainer t
        WHERE t.id NOT IN (
            SELECT trainers.trainer_id
            FROM trainee_trainer trainers
            JOIN trainee tra ON trainers.trainee_id = tra.id
            JOIN users u ON tra.user_id = u.id
            WHERE u.username = :username
        )
        """;
            return session.createNativeQuery(sql, Trainer.class)
                    .setParameter("username", traineeUsername)
                    .getResultList();
    }

}

