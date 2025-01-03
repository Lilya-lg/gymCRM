package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.gym.crm.config.TrainingTypeInitializer;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.TrainingType;
import uz.gym.crm.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TrainerDAOImplTest {

    private TrainerDAOImpl trainerDAO;
    private SessionFactory sessionFactory;
    private Session session;

    @BeforeEach
    void setUp() {
        // Configure Hibernate with H2 database
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Trainer.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Trainee.class);
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
        trainerDAO = new TrainerDAOImpl(session);

        // Clear the database before each test
        Transaction transaction = session.beginTransaction();
        session.createQuery("DELETE FROM Trainer").executeUpdate();
        session.createQuery("DELETE FROM User").executeUpdate();
        transaction.commit();
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
    void findByUser_UsernameAndUser_Password_ShouldReturnTrainer() {
        User user = new User();
        user.setFirstName("TrainerFirst");  // Required field
        user.setLastName("TrainerLast");    // Required field
        user.setUsername("trainerUser");    // Required field
        user.setPassword("trainerPass");    // Required field
        user.setActive(true);               // Assuming this is a required field

        // Create the Trainer and associate the User
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(getTrainingType("Yoga")); // Example specialization

        // Save the Trainer
        Transaction transaction = session.beginTransaction();
        session.save(user);
        session.save(trainer);
        transaction.commit();

        // Test the DAO method
        Optional<Trainer> result = trainerDAO.findByUsernameAndPassword("trainerUser", "trainerPass");
        assertTrue(result.isPresent(), "Trainer should be found");
        assertEquals(user.getUsername(), result.get().getUser().getUsername());
    }

    @Test
    void findByUser_UsernameAndUser_Password_ShouldReturnEmptyOptional() {
        Optional<Trainer> result = trainerDAO.findByUsernameAndPassword("nonExistentUser", "wrongPass");
        assertTrue(result.isEmpty(), "No Trainer should be found for invalid credentials");
    }

    @Test
    void findByUsername_ShouldReturnTrainer() {
        User user = new User(); // No-argument constructor
        user.setFirstName("TrainerFirst");  // Required field
        user.setLastName("TrainerLast");    // Required field
        user.setUsername("trainerUser");    // Required field
        user.setPassword("trainerPass");    // Required field
        user.setActive(true);               // Assuming this is a required field

        // Create the Trainer and associate the User
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(getTrainingType("Yoga")); // Example specialization

        // Save the Trainer
        Transaction transaction = session.beginTransaction();
        session.save(user);
        session.save(trainer);
        transaction.commit();

        // Test the DAO method
        Optional<Trainer> result = trainerDAO.findByUsername("trainerUser");
        assertTrue(result.isPresent(), "Trainer should be found");
        assertEquals(user.getUsername(), result.get().getUser().getUsername());
    }
    private TrainingType getTrainingType(String type) {
        return session.createQuery("FROM TrainingType WHERE trainingType = :type", TrainingType.class)
                .setParameter("type", type)
                .uniqueResult();
    }
    @Test
    void findByUsername_ShouldReturnEmptyOptional() {
        Optional<Trainer> result = trainerDAO.findByUsername("nonExistentUser");
        assertTrue(result.isEmpty(), "No Trainer should be found for invalid username");
    }

    @Test
    void testGetUnassignedTrainersByTraineeUsername() {
        User traineeUser = new User();
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");
        traineeUser.setUsername("traineeUser");
        traineeUser.setPassword("password");
        traineeUser.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);
        trainee.setAddress("123 Street");
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));

        session.persist(traineeUser);
        session.persist(trainee);

        // Create and persist Trainers
        User trainerUser1 = new User();
        trainerUser1.setFirstName("Alice");
        trainerUser1.setLastName("Smith");
        trainerUser1.setUsername("trainer1");
        trainerUser1.setPassword("password");
        trainerUser1.setActive(true);

        Trainer trainer1 = new Trainer();
        trainer1.setUser(trainerUser1);
        trainer1.setSpecialization(getTrainingType("Yoga"));

        session.persist(trainerUser1);
        session.persist(trainer1);

        User trainerUser2 = new User();
        trainerUser2.setFirstName("Bob");
        trainerUser2.setLastName("Johnson");
        trainerUser2.setUsername("trainer2");
        trainerUser2.setPassword("password");
        trainerUser2.setActive(true);

        Trainer trainer2 = new Trainer();
        trainer2.setUser(trainerUser2);
        trainer2.setSpecialization(getTrainingType("Cardio"));

        session.persist(trainerUser2);
        session.persist(trainer2);

        // Assign one trainer to the trainee
        trainee.getTrainers().add(trainer1);
        trainer1.getTrainees().add(trainee);
        session.update(trainee);

        // Synchronize the session with the database
        Transaction transaction = session.beginTransaction(); // Start a new transaction
        session.flush();
        session.clear();
        transaction.commit(); // Commit the transaction

        // Fetch unassigned trainers
        List<Trainer> unassignedTrainers = trainerDAO.getUnassignedTrainersByTraineeUsername("traineeUser");

        // Assertions
        assertEquals(1, unassignedTrainers.size());
        assertEquals("Cardio", unassignedTrainers.get(0).getSpecialization().getTrainingType());
    }


}