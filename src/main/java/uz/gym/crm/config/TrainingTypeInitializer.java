package uz.gym.crm.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import uz.gym.crm.domain.PredefinedTrainingType;
import uz.gym.crm.domain.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TrainingTypeInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingTypeInitializer.class);
    public static void initializeTrainingTypes(SessionFactory sessionFactory) {

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            for (PredefinedTrainingType predefinedType : PredefinedTrainingType.values()) {
                Long count = session.createQuery(
                                "SELECT COUNT(t) FROM TrainingType t WHERE t.trainingType = :type", Long.class)
                        .setParameter("type", predefinedType.getDisplayName())
                        .getSingleResult();
                if (count == 0) {
                    session.persist(new TrainingType(predefinedType.getDisplayName()));
                }
            }

            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Failed to initialize TrainingTypes", e);
            throw new RuntimeException("Failed to initialize TrainingTypes", e);
        }
    }
}
