package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.TrainingType;
import uz.gym.crm.domain.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOImplTest {

    private UserDAOImpl userDAO;
    private SessionFactory sessionFactory;
    private Session session;

    @BeforeEach
    void setUp() {
        // Configure Hibernate with H2 database
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Trainer.class);
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
        userDAO = new UserDAOImpl(session);

        // Clear the database before each test
        Transaction transaction = session.beginTransaction();
        session.createQuery("DELETE FROM User").executeUpdate();
        transaction.commit();
    }

    @AfterEach
    void tearDown() {
        if (session.isOpen()) {
            session.close();
        }
        sessionFactory.close();
    }

    @Test
    void findByUsername_ShouldReturnUser() {
        User user = new User();
        user.setFirstName("Test");       // Required field
        user.setLastName("User");        // Required field
        user.setUsername("testUser");    // Required field
        user.setPassword("testPass");    // Required field
        user.setActive(true);            // Assuming this is a required field

        // Save the user
        Transaction transaction = session.beginTransaction();
        session.save(user);
        transaction.commit();

        // Test the DAO method
        Optional<User> result = userDAO.findByUsername("testUser");
        assertTrue(result.isPresent(), "User should be found");
        assertEquals("testUser", result.get().getUsername());
    }

    @Test
    void findByUsername_ShouldReturnEmptyOptional() {
        // Test the DAO method with a non-existent username
        Optional<User> result = userDAO.findByUsername("nonExistentUser");
        assertTrue(result.isEmpty(), "No User should be found for invalid username");
    }

    @Test
    void findByUsernameAndPassword_ShouldReturnUser() {
        User user = new User();
        user.setFirstName("Test");       // Required field
        user.setLastName("User");        // Required field
        user.setUsername("testUser");   // Required field
        user.setPassword("testPass");   // Required field
        user.setActive(true);           // Assuming this is required

        // Save the user
        Transaction transaction = session.beginTransaction();
        session.save(user);
        transaction.commit();

        // Test the DAO method
        Optional<User> result = userDAO.findByUsernameAndPassword("testUser", "testPass");
        assertTrue(result.isPresent(), "User should be found");
        assertEquals("testUser", result.get().getUsername());
    }

    @Test
    void findByUsernameAndPassword_ShouldReturnEmptyOptional() {
        // Test the DAO method with invalid credentials
        Optional<User> result = userDAO.findByUsernameAndPassword("nonExistentUser", "wrongPass");
        assertTrue(result.isEmpty(), "No User should be found for invalid credentials");
    }
}

