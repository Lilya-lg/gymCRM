package uz.gym.crm.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import uz.gym.crm.domain.TrainingType;

import java.util.List;

public class TrainingTypeInitializer {
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
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize TrainingTypes", e);
        }
    }
}
