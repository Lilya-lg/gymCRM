package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import uz.gym.crm.dao.BaseDAO;
import uz.gym.crm.domain.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class BaseServiceImplTest {

    private static class TestBaseServiceImpl extends BaseServiceImpl<User> {
        public TestBaseServiceImpl(BaseDAO<User> dao) {
            super(dao);
        }
    }

    private BaseServiceImpl<User> baseService;

    @Mock
    private BaseDAO<User> mockDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        baseService = new TestBaseServiceImpl(mockDao); // Use concrete subclass
    }

    @Test
    void testCreate() {
        User user = createTestUser(1L);

        baseService.create(user);

        verify(mockDao).create(user);
    }

    @Test
    void testRead_ExistingEntity() {
        User user = createTestUser(1L);
        when(mockDao.read(1L)).thenReturn(Optional.of(user));

        User result = baseService.read(1L);

        assertNotNull(result);
        assertEquals(user, result);
        verify(mockDao).read(1L);
    }

    @Test
    void testRead_NonExistingEntity() {
        when(mockDao.read(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> baseService.read(1L));

        assertEquals("Entity not found for ID: 1", exception.getMessage());
        verify(mockDao).read(1L);
    }

    @Test
    void testUpdate() {
        User user = createTestUser(1L);

        baseService.update(user);

        verify(mockDao).update(user);
    }

    @Test
    void testDelete() {
        baseService.delete(1L);

        verify(mockDao).delete(1L);
    }

    @Test
    void testGetAll() {
        User user1 = createTestUser(1L);
        User user2 = createTestUser(2L);

        List<User> users = Arrays.asList(user1, user2);
        when(mockDao.getAll()).thenReturn(users);

        List<User> result = baseService.getAll();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(users));
        verify(mockDao).getAll();
    }

    private User createTestUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setPassword("password");
        user.setActive(true);
        return user;
    }
    @Test
    void testCreate_ExceptionThrown() {
        User user = createTestUser(1L);
        doThrow(new RuntimeException("Database error")).when(mockDao).create(user);

        Exception exception = assertThrows(RuntimeException.class, () -> baseService.create(user));

        assertEquals("Database error", exception.getMessage());
        verify(mockDao).create(user);
    }

    @Test
    void testRead_ExceptionThrown() {
        when(mockDao.read(1L)).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> baseService.read(1L));

        assertEquals("Database error", exception.getMessage());
        verify(mockDao).read(1L);
    }

    @Test
    void testUpdate_ExceptionThrown() {
        User user = createTestUser(1L);
        doThrow(new RuntimeException("Database error")).when(mockDao).update(user);

        Exception exception = assertThrows(RuntimeException.class, () -> baseService.update(user));

        assertEquals("Database error", exception.getMessage());
        verify(mockDao).update(user);
    }

    @Test
    void testDelete_ExceptionThrown() {
        doThrow(new RuntimeException("Database error")).when(mockDao).delete(1L);

        Exception exception = assertThrows(RuntimeException.class, () -> baseService.delete(1L));

        assertEquals("Database error", exception.getMessage());
        verify(mockDao).delete(1L);
    }

    @Test
    void testGetAll_ExceptionThrown() {
        when(mockDao.getAll()).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> baseService.getAll());

        assertEquals("Database error", exception.getMessage());
        verify(mockDao).getAll();
    }

}
