package uz.gym.crm.config;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.gym.crm.dao.BaseDAOImpl;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUtil.class);

    static {
        try {
            sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
            TrainingTypeInitializer.initializeTrainingTypes(sessionFactory);

        } catch (HibernateException ex) {
            LOGGER.error("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }


    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
