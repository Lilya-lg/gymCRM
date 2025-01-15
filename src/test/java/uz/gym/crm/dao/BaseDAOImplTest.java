package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.gym.crm.domain.User;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BaseDAOImplTest {
    private BaseDAOImpl<User> userDAO;
    private SessionFactory sessionFactory;
    private Session session;

    @BeforeEach
    void setUp() {
        // Configure Hibernate with H2 database
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(User.class);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show_sql", "true");

        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
        userDAO = new BaseDAOImpl<>(User.class, session);
    }

    @AfterEach
    void tearDown() {
        if (session.isOpen()) {
            session.close();
        }
        sessionFactory.close();
    }

    @Test
    void save_ShouldSaveEntity() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setPassword("password");
        user.setActive(true);

        userDAO.save(user);

        User result = session.get(User.class, user.getId());
        assertNotNull(result, "User should be saved");
        assertEquals("johndoe", result.getUsername());
    }

    @Test
    void read_ShouldReturnEntity() {
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setUsername("janedoe");
        user.setPassword("password");
        user.setActive(true);

        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();

        Optional<User> result = userDAO.read(user.getId());
        assertTrue(result.isPresent(), "User should be found");
        assertEquals("janedoe", result.get().getUsername());
    }

    @Test
    void getAll_ShouldReturnListOfEntities() {
        User user1 = new User();
        user1.setFirstName("Alice");
        user1.setLastName("Smith");
        user1.setUsername("alicesmith");
        user1.setPassword("password");
        user1.setActive(true);

        User user2 = new User();
        user2.setFirstName("Bob");
        user2.setLastName("Johnson");
        user2.setUsername("bobjohnson");
        user2.setPassword("password");
        user2.setActive(true);

        session.beginTransaction();
        session.save(user1);
        session.save(user2);
        session.getTransaction().commit();

        List<User> users = userDAO.getAll();
        assertEquals(2, users.size(), "There should be 2 users");
    }

    @Test
    void update_ShouldUpdateEntity() {
        User user = new User();
        user.setFirstName("Charlie");
        user.setLastName("Brown");
        user.setUsername("charliebrown");
        user.setPassword("password");
        user.setActive(true);

        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();

        user.setUsername("updatedusername");
        userDAO.update(user);

        User result = session.get(User.class, user.getId());
        assertEquals("updatedusername", result.getUsername(), "Username should be updated");
    }

    @Test
    void delete_ShouldDeleteEntity() {
        User user = new User();
        user.setFirstName("David");
        user.setLastName("Clark");
        user.setUsername("davidclark");
        user.setPassword("password");
        user.setActive(true);

        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();

        userDAO.delete(user.getId());

        User result = session.get(User.class, user.getId());
        assertNull(result, "User should be deleted");
    }

    @Test
    void existsById_ShouldReturnTrueIfEntityExists() {
        User user = new User();
        user.setFirstName("Eve");
        user.setLastName("Taylor");
        user.setUsername("evetaylor");
        user.setPassword("password");
        user.setActive(true);

        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();

        assertTrue(userDAO.existsById(user.getId()), "User should exist");
    }

    @Test
    void findByUsername_ShouldReturnEntity() {
        User user = new User();
        user.setFirstName("Frank");
        user.setLastName("Moore");
        user.setUsername("frankmoore");
        user.setPassword("password");
        user.setActive(true);

        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();

        Optional<User> result = userDAO.findByUsername("frankmoore");
        assertTrue(result.isPresent(), "User should be found");
        assertEquals("Frank", result.get().getFirstName());
    }
}


