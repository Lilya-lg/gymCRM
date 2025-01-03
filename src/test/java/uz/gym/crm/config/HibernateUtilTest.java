package uz.gym.crm.config;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.internal.util.config.ConfigurationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.hibernate.cfg.Configuration;

import static org.junit.jupiter.api.Assertions.*;

class HibernateUtilTest {

    @Test
    void getSessionFactory_ShouldReturnSessionFactory() {
        // Verify that the SessionFactory is not null
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        assertNotNull(sessionFactory, "SessionFactory should not be null");

        // Verify that the SessionFactory is open
        assertTrue(sessionFactory.isOpen(), "SessionFactory should be open");
    }

    @Test
    void getSessionFactory_ShouldThrowExceptionIfConfigurationFails() {
        HibernateUtil.shutdown(); // Shutdown the existing SessionFactory

        ConfigurationException exception = assertThrows(ConfigurationException.class, () -> {
            // Attempt to initialize HibernateUtil with an invalid configuration
            new Configuration().configure("invalid-hibernate.cfg.xml").buildSessionFactory();
        });

        // Verify the exception message
        assertNotNull(exception.getMessage(), "Exception message should not be null");
        assertTrue(exception.getMessage().contains("Could not locate cfg.xml resource"),
                "Exception message should indicate missing configuration file");
    }

    @AfterAll
    static void tearDown() {
        // Shutdown the SessionFactory
        HibernateUtil.shutdown();
    }
}
