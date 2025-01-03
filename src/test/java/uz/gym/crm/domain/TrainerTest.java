package uz.gym.crm.domain;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import uz.gym.crm.config.TrainingTypeInitializer;
import uz.gym.crm.domain.User;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TrainerTest {
    private Trainer trainer;
    private TrainingType trainingType;
    private User user;
    private Trainee trainee;

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
        trainer = new Trainer();


        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john_doe");
        user.setPassword("password");

        trainee = new Trainee();
        trainee.setId(1L);

        trainer.setSpecialization(getTrainingType("Yoga"));
        trainer.setUser(user);

    }

    @AfterEach
    void tearDown() {
        if (session.isOpen()) {
            session.close();
        }
        sessionFactory.close();
    }

    @Test
    void getAndSetSpecialization_ShouldWorkCorrectly() {
        // Test initial specialization
        assertEquals("Yoga", trainer.getSpecialization().getTrainingType());

        // Change specialization and test
        trainer.setSpecialization(getTrainingType("Cardio"));

        assertEquals("Cardio", trainer.getSpecialization().getTrainingType());
    }

    @Test
    void getAndSetUser_ShouldWorkCorrectly() {
        // Test initial user
        assertEquals("John", trainer.getUser().getFirstName());
        assertEquals("Doe", trainer.getUser().getLastName());

        // Change user and test
        User newUser = new User();
        newUser.setId(2L);
        newUser.setFirstName("Jane");
        newUser.setLastName("Smith");
        trainer.setUser(newUser);

        assertEquals("Jane", trainer.getUser().getFirstName());
        assertEquals("Smith", trainer.getUser().getLastName());
    }

    @Test
    void getAndSetTrainees_ShouldWorkCorrectly() {
        // Test initial trainees
        assertTrue(trainer.getTrainees().isEmpty(), "Trainer should initially have no trainees");

        // Add a trainee
        Set<Trainee> trainees = new HashSet<>();
        trainees.add(trainee);
        trainer.setTrainees(trainees);

        assertEquals(1, trainer.getTrainees().size(), "Trainer should have one trainee");
        assertTrue(trainer.getTrainees().contains(trainee), "Trainer should contain the added trainee");
    }

    @Test
    void addTrainee_ShouldSynchronizeRelationship() {
        // Add trainee directly to the set
        trainer.getTrainees().add(trainee);

        assertEquals(1, trainer.getTrainees().size(), "Trainer should have one trainee after adding directly");
        assertTrue(trainer.getTrainees().contains(trainee), "Trainer's trainees should include the added trainee");
    }

    @Test
    void setId_ShouldUpdateId() {
        // Test initial ID
        assertNull(trainer.getId(), "Trainer's ID should initially be null");

        // Set ID
        trainer.setId(5L);

        assertEquals(5L, trainer.getId(), "Trainer's ID should be updated to 5");
    }

}
