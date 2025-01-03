package uz.gym.crm.domain;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.gym.crm.config.TrainingTypeInitializer;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTest {

    private SessionFactory sessionFactory;
    private Session session;
    private TrainingType getTrainingType(String type) {
        return session.createQuery("FROM TrainingType WHERE trainingType = :type", TrainingType.class)
                .setParameter("type", type)
                .uniqueResult();
    }
    @BeforeEach
    void setUp() {
        // Configure Hibernate with H2 database
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Training.class);
        configuration.addAnnotatedClass(Trainee.class);
        configuration.addAnnotatedClass(Trainer.class);
        configuration.addAnnotatedClass(TrainingType.class);
        configuration.addAnnotatedClass(User.class);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show_sql", "true");

        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
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
    void testPersistTraining() {
        User traineeUser = new User();
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");
        traineeUser.setUsername("traineeUser");
        traineeUser.setPassword("password");
        traineeUser.setActive(true); // Assuming there's an active field

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        User trainerUser = new User();
        trainerUser.setFirstName("Alice");
        trainerUser.setLastName("Smith");
        trainerUser.setUsername("trainerUser");
        trainerUser.setPassword("password");
        trainerUser.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(getTrainingType("Cardio"));

        //TrainingType trainingType = new TrainingType("Cardio");

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Morning Cardio");
        training.setTrainingType(getTrainingType("Cardio"));
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);

        // Save entities
        Transaction transaction = session.beginTransaction();
        session.save(traineeUser);
        session.save(trainee);
        session.save(trainerUser);
        session.save(trainer);
       // session.save(getTrainingType("Cardio"));
        session.save(training);
        transaction.commit();

        // Fetch the saved Training
        Training savedTraining = session.get(Training.class, training.getId());
        assertNotNull(savedTraining, "Training should be saved in the database");
        assertEquals("Morning Cardio", savedTraining.getTrainingName());
        assertEquals("Cardio", savedTraining.getTrainingType().getTrainingType());
        assertEquals("traineeUser", savedTraining.getTrainee().getUser().getUsername());
        assertEquals("trainerUser", savedTraining.getTrainer().getUser().getUsername());
    }

    @Test
    void testTrainingRelationships() {
        User traineeUser = new User();
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");
        traineeUser.setUsername("traineeUser");
        traineeUser.setPassword("password");
        traineeUser.setActive(true); // Assuming there's an active field

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        User trainerUser = new User();
        trainerUser.setFirstName("Alice");
        trainerUser.setLastName("Smith");
        trainerUser.setUsername("trainerUser");
        trainerUser.setPassword("password");
        trainerUser.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(getTrainingType("Pilates")); // Ensure specialization is set

        //TrainingType trainingType = new TrainingType("Strength");

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Evening Strength Training");
        training.setTrainingType(getTrainingType("Pilates"));
        training.setTrainingDate(LocalDate.of(2024, 12, 28));
        training.setTrainingDuration(90);

        // Save entities
        Transaction transaction = session.beginTransaction();
        session.save(traineeUser);
        session.save(trainee);
        session.save(trainerUser);
        session.save(trainer);
        //session.save(trainingType);
        session.save(training);
        transaction.commit();

        // Verify relationships
        Training fetchedTraining = session.get(Training.class, training.getId());
        assertNotNull(fetchedTraining, "Training should exist");
        assertEquals("traineeUser", fetchedTraining.getTrainee().getUser().getUsername());
        assertEquals("trainerUser", fetchedTraining.getTrainer().getUser().getUsername());
        assertEquals("Pilates", fetchedTraining.getTrainingType().getTrainingType());
    }
}
