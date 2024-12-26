package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uz.gym.crm.domain.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BaseDAOImplTest {
/*
    private BaseDAOImpl<TestEntity> baseDAO;

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        baseDAO = new BaseDAOImpl<>(TestEntity.class, sessionFactory);
    }

    @Test
    void save_ShouldSaveEntity() {
        TestEntity entity = new TestEntity(1L, "testEntity");

        baseDAO.save(entity);

        verify(session).save(entity);
        verify(transaction).commit();
    }

    @Test
    void read_ShouldReturnEntity() {
        Long id = 1L;
        TestEntity entity = new TestEntity(id, "testEntity");
        when(session.get(TestEntity.class, id)).thenReturn(entity);

        Optional<TestEntity> result = baseDAO.read(id);

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
    }

    @Test
    void getAll_ShouldReturnListOfEntities() {
        // Arrange
        List<TestEntity> entities = List.of(
                new TestEntity(1L, "testEntity1"),
                new TestEntity(2L, "testEntity2")
        );

        // Mock the Query object
        Query<TestEntity> queryMock = mock(Query.class);
        when(session.createQuery("FROM TestEntity", TestEntity.class)).thenReturn(queryMock);
        when(queryMock.list()).thenReturn(entities);

        // Act
        List<TestEntity> result = baseDAO.getAll();

        // Assert
        assertEquals(entities.size(), result.size());
        assertEquals(entities, result);
    }

    @Test
    void update_ShouldUpdateEntity() {
        TestEntity entity = new TestEntity(1L, "updatedEntity");

        baseDAO.update(entity);

        verify(session).update(entity);
        verify(transaction).commit();
    }

    @Test
    void delete_ShouldDeleteEntity() {
        Long id = 1L;
        TestEntity entity = new TestEntity(id, "testEntity");
        when(session.get(TestEntity.class, id)).thenReturn(entity);

        baseDAO.delete(id);

        verify(session).delete(entity);
        verify(transaction).commit();
    }

    @Test
    void existsById_ShouldReturnTrueIfExists() {
        Long id = 1L;
        when(session.createQuery("SELECT COUNT(e) > 0 FROM TestEntity e WHERE e.id = :id", Boolean.class)
                .setParameter("id", id)
                .uniqueResult()).thenReturn(true);

        boolean exists = baseDAO.existsById(id);

        assertTrue(exists);
    }

    @Test
    void findByUsername_ShouldReturnEntity() {
        String username = "testUsername";
        TestEntity entity = new TestEntity(1L, username);
        when(session.createQuery("FROM TestEntity WHERE username = :username", TestEntity.class)
                .setParameter("username", username)
                .uniqueResult()).thenReturn(entity);

        Optional<TestEntity> result = baseDAO.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
    }

    // TestEntity class for testing purposes
    static class TestEntity {
        private Long id;
        private String username;

        public TestEntity(Long id, String username) {
            this.id = id;
            this.username = username;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestEntity that = (TestEntity) o;
            return id.equals(that.id) && username.equals(that.username);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, username);
        }

    }

 */
}
