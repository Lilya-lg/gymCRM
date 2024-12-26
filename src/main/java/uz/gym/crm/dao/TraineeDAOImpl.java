package uz.gym.crm.dao;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import uz.gym.crm.config.HibernateUtil;
import uz.gym.crm.domain.Trainee;

import java.util.Optional;

@Repository
public class TraineeDAOImpl extends BaseDAOImpl<Trainee> {
    public TraineeDAOImpl() {
        super(Trainee.class);
    }
    public Optional<Trainee> findByUser_UsernameAndUser_Password(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String query = "SELECT t FROM Trainee t JOIN t.user u " +
                    "WHERE u.username = :username AND u.password = :password";
            Trainee trainee = session.createQuery(query, Trainee.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .uniqueResult();
            return Optional.ofNullable(trainee);
        }
    }
    public Optional<Trainee> findByUser_Username(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String query = "SELECT t FROM Trainee t JOIN t.user u WHERE u.username = :username";
            Trainee trainee = session.createQuery(query, Trainee.class)
                    .setParameter("username", username)
                    .uniqueResult();
            return Optional.ofNullable(trainee);
        }
    }
    @Override
    public Optional<Trainee> findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String query = "SELECT t FROM Trainee t JOIN t.user u WHERE u.username = :username";
            Trainee result = session.createQuery(query, Trainee.class)
                    .setParameter("username", username)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }
}
