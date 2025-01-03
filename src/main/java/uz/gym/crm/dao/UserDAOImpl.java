package uz.gym.crm.dao;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import uz.gym.crm.domain.User;

import java.util.Optional;


@Repository
public class UserDAOImpl extends BaseDAOImpl<User> implements UserDAO {
    private final Session session;
    private static final String FIND_BY_USERNAME = "FROM User WHERE username = :username";
    private static final String FIND_BY_USERNAME_AND_PASSWORD = "FROM User WHERE username = :username AND password = :password";

    public UserDAOImpl(Session session) {
        super(User.class, session);
        this.session = session;
    }

    public Optional<User> findByUsername(String username) {
        User user = session.createQuery(FIND_BY_USERNAME, User.class)
                .setParameter("username", username)
                .uniqueResult();
        return Optional.ofNullable(user);
    }

    public Optional<User> findByUsernameAndPassword(String username, String password) {
        User user = session.createQuery(FIND_BY_USERNAME_AND_PASSWORD, User.class)
                .setParameter("username", username)
                .setParameter("password", password)
                .uniqueResult();
        return Optional.ofNullable(user);

    }
}

