package uz.gym.crm.dao;
import uz.gym.crm.dao.TestEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import uz.gym.crm.config.HibernateUtil;
import uz.gym.crm.domain.BaseEntity;
import uz.gym.crm.domain.Trainee;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BaseDAOImplTest {

    private BaseDAOImpl<TestEntity> baseDAO;
    private SessionFactory sessionFactory;
    private Session session;

    @BeforeEach
    void setUp() {
        // Configure Hibernate for testing with an in-memory database
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(TestEntity.class); // Add the test entity
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
        session.createQuery("DELETE FROM TestEntity").executeUpdate();
        transaction.commit();
        session.clear();
        baseDAO = new BaseDAOImpl(TestEntity.class, session);
    }

    @Test
    void save_ShouldSaveEntity() {
        TestEntity entity = new TestEntity("testEntity");

        Transaction transaction = session.beginTransaction();
        baseDAO.save(entity); // Save without starting a new transaction
        transaction.commit();

        // Retrieve and validate the saved entity
        TestEntity result = session.get(TestEntity.class, entity.getId());
        assertNotNull(result);
        assertEquals(entity.getUsername(), result.getUsername());
    }

    @Test
    void read_ShouldReturnEntity() {
        TestEntity entity = new TestEntity( "testEntity");

        // Save through baseDAO
        baseDAO.save(entity);

        // Read through baseDAO
        Optional<TestEntity> result = baseDAO.read(entity.getId());
        assertTrue(result.isPresent());
        assertEquals(entity.getUsername(), result.get().getUsername());
    }

    @Test
    void getAll_ShouldReturnListOfEntities() {
        TestEntity entity1 = new TestEntity("testEntity1");
        TestEntity entity2 = new TestEntity("testEntity2");

        // Use the DAO to save entities
        baseDAO.save(entity1);
        baseDAO.save(entity2);

        // Fetch all entities using the DAO
        List<TestEntity> result = baseDAO.getAll();

        // Verify the results
        assertEquals(2, result.size()); // Only 2 entities should exist
        assertTrue(result.stream().anyMatch(e -> "testEntity1".equals(e.getUsername())));
        assertTrue(result.stream().anyMatch(e -> "testEntity2".equals(e.getUsername())));
    }

    @Test
    void update_ShouldUpdateEntity() {
        TestEntity entity = new TestEntity( "testEntity");
        Transaction transaction = session.beginTransaction();
        session.save(entity);
        transaction.commit();

        entity.setUsername("updatedEntity");
        baseDAO.update(entity);

        TestEntity result = session.get(TestEntity.class, entity.getId());
        assertEquals("updatedEntity", result.getUsername());
    }

    @Test
    void delete_ShouldDeleteEntity() {
        TestEntity entity = new TestEntity("testEntity");
        Transaction transaction = session.beginTransaction();
        session.save(entity);
        transaction.commit();

        baseDAO.delete(entity.getId());

        TestEntity result = session.get(TestEntity.class, entity.getId());
        assertNull(result);
    }

    @Test
    void existsById_ShouldReturnTrueIfExists() {
        TestEntity entity = new TestEntity();
        entity.setUsername("testEntity");

        Transaction transaction = session.beginTransaction();
        session.save(entity);
        transaction.commit();

        boolean exists = baseDAO.existsById(entity.getId());
        assertTrue(exists);
    }

    @Test
    void findByUsername_ShouldReturnEntity() {
        TestEntity entity = new TestEntity();
        entity.setUsername("testUsername");

        // Save the entity
        Transaction transaction = session.beginTransaction();
        session.save(entity);
        transaction.commit();

        // Clear the session to ensure no caching issues
        session.clear();

        // Find by username
        Optional<TestEntity> result = baseDAO.findByUsername("testUsername");

        assertTrue(result.isPresent(), "Entity should be present");
        assertEquals(entity.getUsername(), result.get().getUsername());
    }


    // TestEntity class for testing purposes
    @Test
    void save_ShouldHandleTransaction() {
        TestEntity entity = new TestEntity("transactionEntity");

        // Simulate an active transaction
        Transaction transaction = session.beginTransaction();
        baseDAO.save(entity); // Save should not start a new transaction
        transaction.commit();

        TestEntity result = session.get(TestEntity.class, entity.getId());
        assertNotNull(result, "Entity should be saved within an active transaction");
        assertEquals("transactionEntity", result.getUsername());
    }

    @Test
    void save_ShouldThrowExceptionOnFailure() {
        TestEntity entity = null; // Invalid entity to trigger an exception

        Exception exception = assertThrows(Exception.class, () -> {
            baseDAO.save(entity);
        });

        assertNotNull(exception, "Exception should be thrown when saving a null entity");
    }

    @Test
    void existsById_ShouldReturnFalseIfNotExists() {
        boolean exists = baseDAO.existsById(999L); // Assuming 999L doesn't exist
        assertFalse(exists, "existsById should return false for non-existent ID");
    }

    @Test
    void findByUsername_ShouldReturnEmptyOptionalForNonExistentUser() {
        Optional<TestEntity> result = baseDAO.findByUsername("nonExistentUsername");
        assertTrue(result.isEmpty(), "findByUsername should return empty for a non-existent username");
    }

}


