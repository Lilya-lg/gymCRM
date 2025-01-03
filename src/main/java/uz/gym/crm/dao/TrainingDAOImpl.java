package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;


import java.time.LocalDate;
import java.util.List;

@Repository
public class TrainingDAOImpl extends BaseDAOImpl<Training> implements TrainingDAO{
    private final Session session;
    private static final String FIND_BY_CRITERIA =
            """
            SELECT t FROM Training t 
            WHERE t.trainee = :trainee 
            AND (:trainer IS NULL OR CONCAT(t.trainer.user.firstName, ' ', t.trainer.user.lastName) = :trainer) 
            AND (:fromDate IS NULL OR t.trainingDate >= :fromDate) 
            AND (:toDate IS NULL OR t.trainingDate <= :toDate)
            """;

    private static final String FIND_BY_CRITERIA_FOR_TRAINER =
            """
            SELECT t FROM Training t 
            WHERE t.trainer = :trainer 
            AND (:trainerName IS NULL OR CONCAT(t.trainer.user.firstName, ' ', t.trainer.user.lastName) = :trainerName) 
            AND (:trainingType IS NULL OR t.trainingType.trainingType = :trainingType) 
            AND (:fromDate IS NULL OR t.trainingDate >= :fromDate) 
            AND (:toDate IS NULL OR t.trainingDate <= :toDate)
            """;

    public TrainingDAOImpl(Session session) {
        super(Training.class, session);
        this.session = session;
    }

    public List<Training> findByCriteria(Trainee trainee, String trainer, LocalDate fromDate, LocalDate toDate) {
        Query<Training> query = session.createQuery(FIND_BY_CRITERIA, Training.class);
        query.setParameter("trainee", trainee);
        query.setParameter("trainer", trainer);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        return query.getResultList();

    }

    public List<Training> findByCriteriaForTrainer(Trainer trainer, String trainerName, String trainingType, LocalDate fromDate, LocalDate toDate) {
        Query<Training> query = session.createQuery(FIND_BY_CRITERIA_FOR_TRAINER, Training.class);
        query.setParameter("trainer", trainer);
        query.setParameter("trainerName", trainerName);
        query.setParameter("trainingType", trainingType);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        return query.getResultList();

    }
}

