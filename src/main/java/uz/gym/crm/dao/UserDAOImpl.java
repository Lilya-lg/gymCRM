package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uz.gym.crm.dao.abstr.UserDAO;
import uz.gym.crm.domain.User;
import uz.gym.crm.util.DynamicQueryBuilder;


import java.util.Optional;


@Repository
public class UserDAOImpl extends BaseDAOImpl<User> implements UserDAO {
    private static final String FIND_BY_USERNAME = "FROM User WHERE username = :username";
    private static final String FIND_BY_USERNAME_AND_PASSWORD = "FROM User WHERE username = :username AND password = :password";
    private final SessionFactory sessionFactory;

    @Autowired
    public UserDAOImpl(SessionFactory sessionFactory) {
        super(User.class, sessionFactory);
        this.sessionFactory = sessionFactory;
    }
    public Optional<User> findByUsername(String username) {
        DynamicQueryBuilder<User> queryBuilder = new DynamicQueryBuilder<>(FIND_BY_USERNAME);
        queryBuilder.addCondition("username = :username", "username", username);

        return Optional.ofNullable(queryBuilder.buildQuery(getSession(), User.class).uniqueResult());
    }

    @Override
    public Optional<User> findByUsernameAndPassword(String username, String password) {
        DynamicQueryBuilder<User> queryBuilder = new DynamicQueryBuilder<>(FIND_BY_USERNAME_AND_PASSWORD);
        queryBuilder.addCondition("username = :username", "username", username).addCondition("password = :password", "password", password);

        return Optional.ofNullable(queryBuilder.buildQuery(getSession(), User.class).uniqueResult());
    }

    @Override
    public void updateUser(User user) {
        Session session = this.sessionFactory.getCurrentSession();
        session.saveOrUpdate(user);
    }
    private Session getSession() {
        return sessionFactory.openSession();
    }

}

