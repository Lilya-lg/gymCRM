package uz.gym.crm.dao;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import uz.gym.crm.dao.abstr.TrainerDAO;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.util.DynamicQueryBuilder;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDAOImpl extends BaseDAOImpl<Trainer> implements TrainerDAO {
    private final SessionFactory sessionFactory;
    private static final String FIND_BY_USERNAME_AND_PASSWORD = "SELECT t FROM Trainer t JOIN t.user u WHERE u.username = :username AND u.password = :password";
    private static final String FIND_BY_USERNAME = "SELECT t FROM Trainer t JOIN t.user u WHERE u.username = :username";
    private static final String GET_UNASSIGNED_TRAINERS = "SELECT t FROM Trainer t WHERE t.id NOT IN ( SELECT tr.trainer.id FROM Training tr  WHERE tr.trainee.user.username = :username)";

    public TrainerDAOImpl(SessionFactory sessionFactory) {
        super(Trainer.class, sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    public Optional<Trainer> findByUsernameAndPassword(String username, String password) {
        DynamicQueryBuilder<Trainer> queryBuilder = new DynamicQueryBuilder<>(FIND_BY_USERNAME_AND_PASSWORD);
        queryBuilder.addCondition("u.username = :username", "username", username).addCondition("u.password = :password", "password", password);

        return Optional.ofNullable(queryBuilder.buildQuery(getSession(), Trainer.class).uniqueResult());
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        DynamicQueryBuilder<Trainer> queryBuilder = new DynamicQueryBuilder<>(FIND_BY_USERNAME);
        queryBuilder.addCondition("u.username = :username", "username", username);
        return Optional.ofNullable(queryBuilder.buildQuery(getSession(), Trainer.class).uniqueResult());

    }

    @Override
    public List<Trainer> getUnassignedTrainersByTraineeUsername(String traineeUsername) {
        System.out.println(GET_UNASSIGNED_TRAINERS);
        DynamicQueryBuilder<Trainer> queryBuilder = new DynamicQueryBuilder<>(GET_UNASSIGNED_TRAINERS);
        queryBuilder.addCondition("1=1", "username", traineeUsername);

        return queryBuilder.buildQuery(getSession(), Trainer.class).getResultList();
    }
    private Session getSession() {
        return sessionFactory.openSession();
    }
}

