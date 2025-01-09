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
        trainer.setSpecialization(getTrainingType("Yoga"));

        User traineeUser = new User();
        traineeUser.setFirstName("Jane");
        traineeUser.setLastName("Smith");
        traineeUser.setUsername("traineeJane");
        traineeUser.setPassword("password");

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);


        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(getTrainingType("Yoga"));
        training.setTrainingName("Yoga Session");
        training.setTrainingDate(LocalDate.of(2024, 12, 1));
        training.setTrainingDuration(60);


        Transaction transaction = session.beginTransaction();
        session.save(trainerUser);
        session.save(trainer);
        session.save(traineeUser);
        session.save(trainee);
        session.save(training);
        transaction.commit();


        List<Training> results = trainingDAO.findByCriteria(
                "traineeJane", "Yoga", LocalDate.of(2024, 12, 1), LocalDate.of(2024, 12, 31), "trainerJohn");

        // Assertions
        assertEquals(1, results.size());
        assertEquals("Yoga", results.get(0).getTrainingType().getTrainingType());
        assertEquals("Yoga Session", results.get(0).getTrainingName());
    }

    @Test
    void findByCriteria_ShouldReturnEmptyListForNoMatches() {
        List<Training> results = trainingDAO.findByCriteria(
                "nonexistentTrainee", "NonexistentType", LocalDate.now(), LocalDate.now().plusDays(10), "nonexistentTrainer");

        // Assertions
        assertTrue(results.isEmpty(), "Expected no matching trainings");
    }

    @Test
    void findByCriteriaForTrainer_ShouldReturnMatchingTrainings() {
        User trainerUser = new User();
        trainerUser.setFirstName("John");
        trainerUser.setLastName("Doe");
        trainerUser.setUsername("trainerJohn");
        trainerUser.setPassword("password");

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(getTrainingType("Yoga"));

        User traineeUser = new User();
        traineeUser.setFirstName("Jane");
        traineeUser.setLastName("Smith");
        traineeUser.setUsername("traineeJane");
        traineeUser.setPassword("password");

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        // Create a training session
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(getTrainingType("Yoga"));
        training.setTrainingName("Yoga Session");
        training.setTrainingDate(LocalDate.of(2024, 12, 1));
        training.setTrainingDuration(60);


        Transaction transaction = session.beginTransaction();
        session.save(trainerUser);
        session.save(trainer);
        session.save(traineeUser);
        session.save(trainee);
        session.save(training);
        transaction.commit();


        List<Training> results = trainingDAO.findByCriteriaForTrainer(
                "trainerJohn", LocalDate.of(2024, 12, 1), LocalDate.of(2024, 12, 31), "traineeJane");


        assertEquals(1, results.size());
        assertEquals("Yoga", results.get(0).getTrainingType().getTrainingType());
        assertEquals("Yoga Session", results.get(0).getTrainingName());
    }

    @Test
    void findByCriteriaForTrainer_ShouldReturnEmptyListForNoMatches() {
        List<Training> results = trainingDAO.findByCriteriaForTrainer(
                "nonexistentTrainer", LocalDate.now(), LocalDate.now().plusDays(10), "nonexistentTrainee");


        assertTrue(results.isEmpty(), "Expected no matching trainings");
    }
}

