package uz.gym.crm.service;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.gym.crm.dao.TestEntity;

import static org.junit.jupiter.api.Assertions.*;

class TestEntityDAOTest {
    private SessionFactory sessionFactory;
    private Session session;

    @BeforeEach
    void setUp() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(TestEntity.class);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show_sql", "true");

        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();

        // Clear the database
        Transaction transaction = session.beginTransaction();
        session.createQuery("DELETE FROM TestEntity").executeUpdate();
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
    void saveEntity_ShouldPersistToDatabase() {
        TestEntity entity = new TestEntity("testuser");

        Transaction transaction = session.beginTransaction();
        session.save(entity);
        transaction.commit();

        assertNotNull(entity.getId(), "ID should be generated after save");
    }

    @Test
    void fetchById_ShouldReturnEntity() {
        TestEntity entity = new TestEntity("testuser");

        Transaction transaction = session.beginTransaction();
        session.save(entity);
        transaction.commit();

        TestEntity fetchedEntity = session.find(TestEntity.class, entity.getId());
        assertNotNull(fetchedEntity, "Entity should be fetched from the database");
        assertEquals("testuser", fetchedEntity.getUsername(), "Fetched username should match");
    }

    @Test
    void updateEntity_ShouldPersistChanges() {
        TestEntity entity = new TestEntity("testuser");

        Transaction transaction = session.beginTransaction();
        session.save(entity);
        transaction.commit();

        // Update entity
        entity.setUsername("updateduser");
        transaction = session.beginTransaction();
        session.update(entity);
        transaction.commit();

        TestEntity updatedEntity = session.find(TestEntity.class, entity.getId());
        assertEquals("updateduser", updatedEntity.getUsername(), "Username should be updated");
    }

    @Test
    void deleteEntity_ShouldRemoveFromDatabase() {
        TestEntity entity = new TestEntity("testuser");

        Transaction transaction = session.beginTransaction();
        session.save(entity);
        transaction.commit();

        transaction = session.beginTransaction();
        session.delete(entity);
        transaction.commit();

        TestEntity deletedEntity = session.find(TestEntity.class, entity.getId());
        assertNull(deletedEntity, "Entity should be deleted from the database");
    }


}
