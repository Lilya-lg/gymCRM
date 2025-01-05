package uz.gym.crm.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import uz.gym.crm.domain.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TrainingTypeInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingTypeInitializer.class);
    public static void initializeTrainingTypes(SessionFactory sessionFactory) {
        List<String> predefinedTypes = List.of("Yoga", "Cardio", "Cycle", "Pilates");

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            for (String type : predefinedTypes) {
                Long count = session.createQuery(
                                "SELECT COUNT(t) FROM TrainingType t WHERE t.trainingType = :type", Long.class)
                        .setParameter("type", type)
                        .getSingleResult();
                if (count == 0) {
                    session.persist(new TrainingType(type));
                }
            }

            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Failed to initialize TrainingTypes", e);
            throw new RuntimeException("Failed to initialize TrainingTypes", e);
        }
    }
}
