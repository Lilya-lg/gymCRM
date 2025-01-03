package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.gym.crm.config.TrainingTypeInitializer;
import uz.gym.crm.domain.TrainingType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTypeDAOImplTest {

    private TrainingTypeDAOImpl trainingTypeDAO;
    private SessionFactory sessionFactory;
    private Session session;

    @BeforeEach
    void setUp() {
        // Configure Hibernate for testing with an in-memory database
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(TrainingType.class);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show_sql", "true");

        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
        trainingTypeDAO = new TrainingTypeDAOImpl(session);

        // Initialize predefined training types
        TrainingTypeInitializer.initializeTrainingTypes(sessionFactory);
    }

    @AfterEach
    void tearDown() {
        if (session.isOpen()) {
            session.close();
        }
        sessionFactory.close();
    }


    @Test
    void getAll_ShouldReturnAllPredefinedTrainingTypes() {
        var trainingTypes = trainingTypeDAO.getAll();
        assertEquals(4, trainingTypes.size(), "Should return all predefined TrainingTypes");
        assertTrue(trainingTypes.stream().anyMatch(t -> "Yoga".equals(t.getTrainingType())));
        assertTrue(trainingTypes.stream().anyMatch(t -> "Cardio".equals(t.getTrainingType())));
        assertTrue(trainingTypes.stream().anyMatch(t -> "Cycle".equals(t.getTrainingType())));
        assertTrue(trainingTypes.stream().anyMatch(t -> "Pilates".equals(t.getTrainingType())));
    }


    @Test
    void read_ShouldReturnTrainingTypeById() {
        var trainingType = session.createQuery("FROM TrainingType WHERE trainingType = :type", TrainingType.class)
                .setParameter("type", "Yoga")
                .uniqueResult();

        Optional<TrainingType> result = trainingTypeDAO.read(trainingType.getId());
        assertTrue(result.isPresent(), "Should return the TrainingType by ID");
        assertEquals("Yoga", result.get().getTrainingType());
    }

    @Test
    void existsById_ShouldReturnTrueForPredefinedType() {
        var trainingType = session.createQuery("FROM TrainingType WHERE trainingType = :type", TrainingType.class)
                .setParameter("type", "Yoga")
                .uniqueResult();

        boolean exists = trainingTypeDAO.existsById(trainingType.getId());
        assertTrue(exists, "existsById should return true for predefined type");
    }

    @Test
    void existsById_ShouldReturnFalseForNonExistentId() {
        boolean exists = trainingTypeDAO.existsById(-1L);
        assertFalse(exists, "existsById should return false for a non-existent ID");
    }
}
