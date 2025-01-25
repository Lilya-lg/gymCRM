package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uz.gym.crm.dao.abstr.TrainingDAO;
import uz.gym.crm.domain.PredefinedTrainingType;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;
import uz.gym.crm.util.DynamicQueryBuilder;


import java.time.LocalDate;
import java.util.List;

@Repository
public class TrainingDAOImpl extends BaseDAOImpl<Training> implements TrainingDAO {
    private static final String BASE_QUERY_FOR_TRAINEE = "SELECT t FROM Training t JOIN t.trainee trainee JOIN t.trainer trainer JOIN trainee.user traineeUser ON trainee.user.id = traineeUser.id JOIN trainer.user trainerUser ON trainer.user.id = trainerUser.id WHERE traineeUser.username = :traineeUsername";
    private static final String BASE_QUERY_FOR_TRAINER = "SELECT t FROM Training t JOIN t.trainer trainer JOIN t.trainee trainee JOIN trainer.user trainerUser ON trainer.user.id = trainerUser.id  JOIN trainee.user traineeUser ON trainee.user.id = traineeUser.id WHERE trainerUser.username = :trainerUsername";
    private static final String CONDITION_TRAINING_TYPE = "t.trainingType.trainingType = :trainingType";
    private static final String CONDITION_FROM_DATE = "t.trainingDate >= :fromDate";
    private static final String CONDITION_TO_DATE = "t.trainingDate <= :toDate";
    private static final String CONDITION_TRAINER_USERNAME = "trainerUser.username = :trainerUsername";
    private static final String CONDITION_TRAINEE_USERNAME = "traineeUser.username = :traineeUsername";
    @Autowired
    private final SessionFactory sessionFactory;

    public TrainingDAOImpl(SessionFactory sessionFactory) {
        super(Training.class, sessionFactory);
        this.sessionFactory = sessionFactory;
    }



    @Override
    public List<Training> findByCriteria(String traineeUsername, PredefinedTrainingType trainingType, LocalDate fromDate, LocalDate toDate, String trainerName) {
        DynamicQueryBuilder<Training> queryBuilder = new DynamicQueryBuilder<>(BASE_QUERY_FOR_TRAINEE);
        queryBuilder.addCondition(CONDITION_TRAINER_USERNAME, "trainerUsername", trainerName).addCondition(CONDITION_TRAINING_TYPE, "trainingType", trainingType).addCondition(CONDITION_FROM_DATE, "fromDate", fromDate).addCondition(CONDITION_TO_DATE, "toDate", toDate);

        Query<Training> query = queryBuilder.buildQuery(getSession(), Training.class);
        query.setParameter("traineeUsername", traineeUsername);

        return query.getResultList();


    }

    @Override
    public List<Training> findByCriteriaForTrainer(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName) {
        DynamicQueryBuilder<Training> queryBuilder = new DynamicQueryBuilder<>(BASE_QUERY_FOR_TRAINER);
        queryBuilder.addCondition(CONDITION_TRAINEE_USERNAME, "traineeUsername", traineeName).addCondition(CONDITION_FROM_DATE, "fromDate", fromDate).addCondition(CONDITION_TO_DATE, "toDate", toDate);

        Query<Training> query = queryBuilder.buildQuery(getSession(), Training.class);
        query.setParameter("trainerUsername", trainerUsername);

        return query.getResultList();
    }

    @Override
    public List<Trainer> findTrainersByTraineeId(Long traineeId) {
        String queryString = "SELECT DISTINCT t.trainer FROM Training t WHERE t.trainee.id = :traineeId";
        Query<Trainer> query = getSession().createQuery(queryString, Trainer.class);
        query.setParameter("traineeId", traineeId);
        return query.getResultList();
    }

    @Override
    public List<Trainee> findTraineesByTrainerId(Long trainerId) {
        String queryString = "SELECT DISTINCT t.trainee FROM Training t WHERE t.trainer.id = :trainerId";
        Query<Trainee> query = getSession().createQuery(queryString, Trainee.class);
        query.setParameter("trainerId", trainerId);
        return query.getResultList();
    }
    private Session getSession() {
        return sessionFactory.openSession();
    }
}

