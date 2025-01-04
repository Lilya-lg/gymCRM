package uz.gym.crm.dao;


import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import uz.gym.crm.domain.Trainer;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDAOImpl extends BaseDAOImpl<Trainer> implements TrainerDAO {
    private final Session session;
    private static final String FIND_BY_USERNAME_AND_PASSWORD = "SELECT t FROM Trainer t JOIN t.user u WHERE u.username = :username AND u.password = :password";
    private static final String FIND_BY_USERNAME = "SELECT t FROM Trainer t JOIN t.user u WHERE u.username = :username";
    private static final String GET_UNASSIGNED_TRAINERS = "SELECT t FROM Trainer t " + "WHERE t.id NOT IN (" + "  SELECT tr.trainer.id FROM Training tr " + "  WHERE tr.trainee.user.username = :username" + ")";

    public TrainerDAOImpl(Session session) {
        super(Trainer.class, session);
        this.session = session;
    }

    public Optional<Trainer> findByUsernameAndPassword(String username, String password) {
        Trainer trainer = session.createQuery(FIND_BY_USERNAME_AND_PASSWORD, Trainer.class).setParameter("username", username).setParameter("password", password).uniqueResult();
        return Optional.ofNullable(trainer);
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        Trainer result = session.createQuery(FIND_BY_USERNAME, Trainer.class).setParameter("username", username).uniqueResult();
        return Optional.ofNullable(result);
    }

    @Override
    public List<Trainer> getUnassignedTrainersByTraineeUsername(String traineeUsername) {
        return session.createQuery(GET_UNASSIGNED_TRAINERS, Trainer.class).setParameter("username", traineeUsername).getResultList();
    }

}

