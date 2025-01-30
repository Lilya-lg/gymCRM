package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.gym.crm.domain.*;

import java.util.List;
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
        configuration.addAnnotatedClass(Trainer.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Trainee.class);
        configuration.addAnnotatedClass(TrainingType.class);
        configuration.addAnnotatedClass(Training.class);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show_sql", "true");

        sessionFactory = configuration.buildSessionFactory();
        if (session != null) {
            session.close();
        }
        session = sessionFactory.openSession();
        trainingTypeDAO = new TrainingTypeDAOImpl(sessionFactory);


        seedTrainingTypes();

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

        List<TrainingType> trainingTypes = trainingTypeDAO.getAll();


        assertEquals(PredefinedTrainingType.values().length, trainingTypes.size(), "Should return all predefined TrainingTypes");
        for (PredefinedTrainingType predefinedType : PredefinedTrainingType.values()) {
            assertTrue(trainingTypes.stream().anyMatch(t -> t.getTrainingType() == predefinedType), "Should contain " + predefinedType.getDisplayName());
        }
    }

    @Test
    void read_ShouldReturnTrainingTypeById() {
        session.beginTransaction(); // Start a transaction
        TrainingType trainingType = session.createQuery("FROM TrainingType WHERE trainingType = :type", TrainingType.class).setParameter("type", PredefinedTrainingType.YOGA).uniqueResult();

        Optional<TrainingType> result = trainingTypeDAO.read(trainingType.getId());
        session.getTransaction().commit();

        assertTrue(result.isPresent(), "Should return the TrainingType by ID");
        assertEquals(PredefinedTrainingType.YOGA.getDisplayName(), result.get().getTrainingType().getDisplayName());
    }

    @Test
    void existsById_ShouldReturnTrueForPredefinedType() {
        session.beginTransaction(); // Start a transaction
        TrainingType trainingType = session.createQuery("FROM TrainingType WHERE trainingType = :type", TrainingType.class).setParameter("type", PredefinedTrainingType.YOGA).uniqueResult();
        session.getTransaction().commit(); // Commit the transaction

        boolean exists = trainingTypeDAO.existsById(trainingType.getId());
        assertTrue(exists, "existsById should return true for predefined type");
    }

    @Test
    void existsById_ShouldReturnFalseForNonExistentId() {
        session.beginTransaction(); // Start a transaction
        boolean exists = trainingTypeDAO.existsById(-1L); // Non-existent ID
        session.getTransaction().commit(); // Commit the transaction

        assertFalse(exists, "existsById should return false for a non-existent ID");
    }

    @Test
    void getOrCreateTrainingType_ShouldReturnExistingTrainingType_WhenPredefinedTypeExists() {
        session.beginTransaction(); // Start a transaction
        TrainingType existingType = trainingTypeDAO.getOrCreateTrainingType(PredefinedTrainingType.YOGA);
        session.getTransaction().commit(); // Commit the transaction

        assertNotNull(existingType, "Should return an existing TrainingType");
        assertEquals(PredefinedTrainingType.YOGA, existingType.getTrainingType(), "Should return the TrainingType corresponding to YOGA");
    }

    @Test
    void getOrCreateTrainingType_ShouldCreateNewTrainingType_WhenPredefinedTypeDoesNotExist() {
        session.beginTransaction();


        PredefinedTrainingType newType = PredefinedTrainingType.fromName("Pilates");
        TrainingType newTrainingType = trainingTypeDAO.getOrCreateTrainingType(newType);

        session.getTransaction().commit();

        assertNotNull(newTrainingType, "Should create a new TrainingType if it doesn't exist");
        assertEquals(newType, newTrainingType.getTrainingType(), "The new TrainingType should match the given predefined type");


        session.beginTransaction();
        Optional<TrainingType> persistedType = trainingTypeDAO.read(newTrainingType.getId());
        session.getTransaction().commit();

        assertTrue(persistedType.isPresent(), "The new TrainingType should be persisted in the database");
        assertEquals(newType, persistedType.get().getTrainingType(), "Persisted type should match the new TrainingType");
    }

    private void seedTrainingTypes() {
        Transaction transaction = session.beginTransaction();
        for (PredefinedTrainingType type : PredefinedTrainingType.values()) {
            TrainingType trainingType = new TrainingType();
            trainingType.setTrainingType(type);
            session.save(trainingType);
        }
        transaction.commit();
    }

}
