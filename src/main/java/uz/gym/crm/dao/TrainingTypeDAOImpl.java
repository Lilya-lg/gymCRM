package uz.gym.crm.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uz.gym.crm.domain.PredefinedTrainingType;
import uz.gym.crm.domain.TrainingType;

@Repository
public class TrainingTypeDAOImpl extends BaseDAOImpl<TrainingType> {
    private final SessionFactory sessionFactory;

    public TrainingTypeDAOImpl(SessionFactory sessionFactory) {

        super(TrainingType.class, sessionFactory);
        this.sessionFactory = sessionFactory;

    }


    public TrainingType getOrCreateTrainingType(PredefinedTrainingType predefinedType) {
        Query<TrainingType> query = getSession().createQuery("FROM TrainingType t WHERE t.trainingType = :predefinedType", TrainingType.class);
        query.setParameter("predefinedType", predefinedType);

        TrainingType trainingType = query.uniqueResult();
        if (trainingType == null) {
            trainingType = new TrainingType();
            trainingType.setTrainingType(predefinedType);
            getSession().persist(trainingType);
        }
        return trainingType;
    }

    private Session getSession() {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            return sessionFactory.getCurrentSession();
        } else {
            return sessionFactory.openSession();
        }
    }

}
