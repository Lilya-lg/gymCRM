package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.gym.crm.config.TrainingTypeInitializer;
import uz.gym.crm.domain.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainingDAOImplTest {

    private TrainingDAOImpl trainingDAO;
    private SessionFactory sessionFactory;
    private Session session;

    @BeforeEach
    void setUp() {
        // Configure Hibernate with H2 database
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Training.class);
        configuration.addAnnotatedClass(Trainer.class);
        configuration.addAnnotatedClass(Trainee.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(TrainingType.class);
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
        // TrainingTypeInitializer.initializeTrainingTypes(HibernateUtil.getSessionFactory());
        // trainingDAO = new TrainingDAOImpl(session);

        // Clear the database before each test
        Transaction transaction = session.beginTransaction();
        session.createQuery("DELETE FROM Training").executeUpdate();
        session.createQuery("DELETE FROM Trainer").executeUpdate();
        session.createQuery("DELETE FROM Trainee").executeUpdate();
        session.createQuery("DELETE FROM User").executeUpdate();
        session.createQuery("DELETE FROM TrainingType").executeUpdate();
        transaction.commit();
        TrainingTypeInitializer.initializeTrainingTypes(sessionFactory);
        trainingDAO = new TrainingDAOImpl(session);
    }
    private TrainingType getTrainingType(String type) {
        return session.createQuery("FROM TrainingType WHERE trainingType = :type", TrainingType.class)
                .setParameter("type", type)
                .uniqueResult();
    }

    @AfterEach
    void tearDown() {
        if (session.isOpen()) {
            session.close();
        }
        sessionFactory.close();
    }

    @Test
    void findByCriteria_ShouldReturnMatchingTrainings() {
        User trainerUser = new User();
        trainerUser.setFirstName("John");
        trainerUser.setLastName("Doe");
        trainerUser.setUsername("trainerJohn");
        trainerUser.setPassword("password");

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(getTrainingType("Yoga")); // Ensure specialization is set

        User traineeUser = new User();
        traineeUser.setFirstName("Jane");
        traineeUser.setLastName("Smith");
        traineeUser.setUsername("traineeJane");
        traineeUser.setPassword("password");

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        // Retrieve the predefined TrainingType (Yoga)
        TrainingType trainingType = session.createQuery(
                        "FROM TrainingType t WHERE t.trainingType = :type", TrainingType.class)
                .setParameter("type", "Yoga")
                .getSingleResult();

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType); // Assign the retrieved TrainingType
        training.setTrainingName("Yoga Session");
        training.setTrainingDate(LocalDate.of(2024, 12, 1));
        training.setTrainingDuration(60);

        // Save entities
        Transaction transaction = session.beginTransaction();
        session.save(trainerUser);
        session.save(trainer);
        session.save(traineeUser);
        session.save(trainee);
        session.save(training);
        transaction.commit();

        // Test the DAO method
        List<Training> results = trainingDAO.findByCriteria(
                trainee, "John Doe", LocalDate.of(2024, 12, 1), LocalDate.of(2024, 12, 31));

        assertEquals(1, results.size());
        assertEquals("Yoga", results.get(0).getTrainingType().getTrainingType());
        assertEquals("Yoga Session", results.get(0).getTrainingName());
    }

    @Test
    void findByCriteria_ShouldReturnEmptyListForNoMatches() {
        User traineeUser = new User();
        traineeUser.setFirstName("Jane");
        traineeUser.setLastName("Smith");
        traineeUser.setUsername("traineeJane");
        traineeUser.setPassword("password");

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        // Save trainee
        Transaction transaction = session.beginTransaction();
        session.save(traineeUser);
        session.save(trainee);
        transaction.commit();

        // Test with criteria that does not match
        List<Training> results = trainingDAO.findByCriteria(trainee, "Nonexistent Trainer", LocalDate.now(), LocalDate.now().plusDays(10));
        assertTrue(results.isEmpty(), "Expected no matching trainings");
    }

    @Test
    void findByCriteriaForTrainer_ShouldReturnMatchingTrainings() {
        User trainerUser = new User();
        trainerUser.setFirstName("John");
        trainerUser.setLastName("Doe");
        trainerUser.setUsername("trainerJohn");
        trainerUser.setPassword("password");
        trainerUser.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(getTrainingType("Yoga"));

        // Query the predefined TrainingType
        TrainingType trainingType = session.createQuery(
                        "FROM TrainingType t WHERE t.trainingType = :type", TrainingType.class)
                .setParameter("type", "Yoga")
                .uniqueResultOptional()
                .orElseThrow(() -> new IllegalStateException("TrainingType 'Yoga' not found in database"));

        Training training = new Training();
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingName("Morning Yoga");
        training.setTrainingDate(LocalDate.of(2024, 12, 1));
        training.setTrainingDuration(60);

        // Save entities
        Transaction transaction = session.beginTransaction();
        session.save(trainerUser);
        session.save(trainer);
        session.save(training);
        transaction.commit();

        // Test the DAO method
        List<Training> results = trainingDAO.findByCriteriaForTrainer(
                trainer,
                "John Doe",
                "Yoga",
                LocalDate.of(2024, 12, 1),
                LocalDate.of(2024, 12, 31)
        );

        // Assertions
        assertEquals(1, results.size(), "Should find one training");
        assertEquals("Yoga", results.get(0).getTrainingType().getTrainingType());
        assertEquals("Morning Yoga", results.get(0).getTrainingName());
        assertEquals(LocalDate.of(2024, 12, 1), results.get(0).getTrainingDate());
    }

    @Test
    void findByCriteriaForTrainer_ShouldReturnEmptyListForNoMatches() {
        User trainerUser = new User();
        trainerUser.setFirstName("John");
        trainerUser.setLastName("Doe");
        trainerUser.setUsername("trainerJohn");
        trainerUser.setPassword("password");

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(getTrainingType("Yoga")); // Set specialization to avoid nullability error

        // Save trainer
        Transaction transaction = session.beginTransaction();
        session.save(trainerUser);
        session.save(trainer);
        transaction.commit();

        // Test with criteria that does not match
        List<Training> results = trainingDAO.findByCriteriaForTrainer(trainer, "Nonexistent Trainer", "Yoga", LocalDate.now(), LocalDate.now().plusDays(10));
        assertTrue(results.isEmpty(), "Expected no matching trainings");
    }
}
