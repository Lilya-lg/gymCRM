package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;
import uz.gym.crm.util.DynamicQueryBuilder;


import java.time.LocalDate;
import java.util.List;

@Repository
public class TrainingDAOImpl extends BaseDAOImpl<Training> implements TrainingDAO {
    private final Session session;
    private static final String BASE_QUERY_FOR_TRAINEE = "SELECT t FROM Training t WHERE t.trainee = :trainee";
    private static final String BASE_QUERY_FOR_TRAINER = "SELECT t FROM Training t WHERE t.trainer = :trainer";
    private static final String CONDITION_TRAINER_NAME = "CONCAT(t.trainer.user.firstName, ' ', t.trainer.user.lastName) = :trainerName";
    private static final String CONDITION_TRAINING_TYPE = "t.trainingType.trainingType = :trainingType";
    private static final String CONDITION_FROM_DATE = "t.trainingDate >= :fromDate";
    private static final String CONDITION_TO_DATE = "t.trainingDate <= :toDate";

    public TrainingDAOImpl(Session session) {
        super(Training.class, session);
        this.session = session;
    }

    @Override
    public List<Training> findByCriteria(Trainee trainee, String trainer, LocalDate fromDate, LocalDate toDate) {
        DynamicQueryBuilder<Training> queryBuilder = new DynamicQueryBuilder<>(BASE_QUERY_FOR_TRAINEE);
        queryBuilder
                .addCondition(CONDITION_TRAINER_NAME, "trainerName", trainer)
                .addCondition(CONDITION_FROM_DATE, "fromDate", fromDate)
                .addCondition(CONDITION_TO_DATE, "toDate", toDate);

        Query<Training> query = queryBuilder.buildQuery(session, Training.class);
        query.setParameter("trainee", trainee);

        return query.getResultList();


    }

    @Override
    public List<Training> findByCriteriaForTrainer(Trainer trainer, String trainerName, String trainingType, LocalDate fromDate, LocalDate toDate) {
        DynamicQueryBuilder<Training> queryBuilder = new DynamicQueryBuilder<>(BASE_QUERY_FOR_TRAINER);
        queryBuilder
                .addCondition(CONDITION_TRAINER_NAME, "trainerName", trainerName)
                .addCondition(CONDITION_TRAINING_TYPE, "trainingType", trainingType)
                .addCondition(CONDITION_FROM_DATE, "fromDate", fromDate)
                .addCondition(CONDITION_TO_DATE, "toDate", toDate);

        Query<Training> query = queryBuilder.buildQuery(session, Training.class);
        query.setParameter("trainer", trainer);

        return query.getResultList();
    }
}

