package uz.gym.crm.dao;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import uz.gym.crm.dao.abstr.UserDAO;
import uz.gym.crm.domain.User;
import uz.gym.crm.util.DynamicQueryBuilder;

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
        DynamicQueryBuilder<User> queryBuilder = new DynamicQueryBuilder<>(FIND_BY_USERNAME);
        queryBuilder.addCondition("username = :username", "username", username);

        return Optional.ofNullable(queryBuilder.buildQuery(session, User.class).uniqueResult());
    }

    public Optional<User> findByUsernameAndPassword(String username, String password) {
        DynamicQueryBuilder<User> queryBuilder = new DynamicQueryBuilder<>(FIND_BY_USERNAME_AND_PASSWORD);
        queryBuilder.addCondition("username = :username", "username", username).addCondition("password = :password", "password", password);

        return Optional.ofNullable(queryBuilder.buildQuery(session, User.class).uniqueResult());

    }
}

