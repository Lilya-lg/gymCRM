package uz.gym.crm.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.gym.crm.domain.BaseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BaseDAOImplTest {

    private BaseDAOImpl<TestEntity> baseDAO;

    @Mock
    private Map<Long, TestEntity> mockStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        baseDAO = new BaseDAOImpl<>(mockStorage, TestEntity::getId) {
        };
    }

    @Test
    void testCreate() {
        TestEntity entity = new TestEntity(1L, "Test Entity");
        baseDAO.create(entity);
        verify(mockStorage).put(1L, entity);
    }

    @Test
    void testRead_ExistingEntity() {
        TestEntity entity = new TestEntity(1L, "Test Entity");
        when(mockStorage.get(1L)).thenReturn(entity);

        Optional<TestEntity> result = baseDAO.read(1L);

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
        verify(mockStorage).get(1L);
    }

    @Test
    void testRead_NonExistingEntity() {
        when(mockStorage.get(1L)).thenReturn(null);

        Optional<TestEntity> result = baseDAO.read(1L);

        assertFalse(result.isPresent());
        verify(mockStorage).get(1L);
    }

    @Test
    void testUpdate_ExistingEntity() {
        TestEntity entity = new TestEntity(1L, "Updated Entity");
        when(mockStorage.containsKey(1L)).thenReturn(true);

        baseDAO.update(entity);

        verify(mockStorage).put(1L, entity);
    }

    @Test
    void testUpdate_NonExistingEntity() {
        TestEntity entity = new TestEntity(1L, "Updated Entity");
        when(mockStorage.containsKey(1L)).thenReturn(false);

        baseDAO.update(entity);

        verify(mockStorage, never()).put(1L, entity);
    }

    @Test
    void testDelete_ExistingEntity() {
        when(mockStorage.remove(1L)).thenReturn(new TestEntity(1L, "Test Entity"));

        baseDAO.delete(1L);

        verify(mockStorage).remove(1L);
    }

    @Test
    void testDelete_NonExistingEntity() {
        when(mockStorage.remove(1L)).thenReturn(null);

        baseDAO.delete(1L);

        verify(mockStorage).remove(1L);
    }

    @Test
    void testGetAll() {
        List<TestEntity> entities = Arrays.asList(new TestEntity(1L, "Entity 1"), new TestEntity(2L, "Entity 2"));
        when(mockStorage.values()).thenReturn(new HashSet<>(entities));

        List<TestEntity> result = baseDAO.getAll();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(entities));
    }

    static class TestEntity extends BaseEntity {
        private final String name;

        public TestEntity(Long id, String name) {
            this.setId(id);
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }
}
