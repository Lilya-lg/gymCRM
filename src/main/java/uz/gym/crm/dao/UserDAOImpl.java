package uz.gym.crm.dao;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import uz.gym.crm.config.HibernateUtil;
import uz.gym.crm.domain.User;

import java.util.Optional;


@Repository
public class UserDAOImpl extends BaseDAOImpl<User> {

    public UserDAOImpl() {
        super(User.class);
    }
    public Optional<User> findByUsername(String username) {
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
            return Optional.ofNullable(user);
        }
    }
    public Optional<User> findByUsernameAndPassword(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String query = "FROM User WHERE username = :username AND password = :password";
            User user = session.createQuery(query, User.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .uniqueResult();
            return Optional.ofNullable(user);
        }
    }
}

