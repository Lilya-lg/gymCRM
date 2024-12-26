package uz.gym.crm.dao;


import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import uz.gym.crm.config.HibernateUtil;
import uz.gym.crm.domain.Trainer;

import java.util.Optional;

@Repository
public class TrainerDAOImpl extends BaseDAOImpl<Trainer> {
    public TrainerDAOImpl() {
        super(Trainer.class);
    }
    public Optional<Trainer> findByUser_UsernameAndUser_Password(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String query = "SELECT t FROM Trainer t JOIN t.user u " +
                    "WHERE u.username = :username AND u.password = :password";
            Trainer trainer = session.createQuery(query, Trainer.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .uniqueResult();
            return Optional.ofNullable(trainer);
        }
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String query = "SELECT t FROM Trainer t JOIN t.user u WHERE u.username = :username";
            Trainer result = session.createQuery(query, Trainer.class)
                    .setParameter("username", username)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }
}
