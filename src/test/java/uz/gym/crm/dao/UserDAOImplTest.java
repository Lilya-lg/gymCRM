package uz.gym.crm.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class UserDAOImplTest {
    private UserDAOImpl userDAO;

    @Mock
    private Map<Long, User> mockStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDAO = new UserDAOImpl(mockStorage);
    }

    @Test
    void testCreate() {
        User user = createTestUser(1L);
        userDAO.create(user);
        verify(mockStorage).put(1L, user);
    }

    @Test
    void testRead_ExistingUser() {
        User user = createTestUser(1L);
        when(mockStorage.get(1L)).thenReturn(user);

        Optional<User> result = userDAO.read(1L);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(mockStorage).get(1L);
    }

    @Test
    void testRead_NonExistingUser() {
        when(mockStorage.get(1L)).thenReturn(null);

        Optional<User> result = userDAO.read(1L);

        assertFalse(result.isPresent());
        verify(mockStorage).get(1L);
    }

    @Test
    void testUpdate_ExistingUser() {
        User user = createTestUser(1L);
        when(mockStorage.containsKey(1L)).thenReturn(true);

        userDAO.update(user);

        verify(mockStorage).put(1L, user);
    }

    @Test
    void testUpdate_NonExistingUser() {
        User user = createTestUser(1L);
        when(mockStorage.containsKey(1L)).thenReturn(false);

        userDAO.update(user);

        verify(mockStorage, never()).put(1L, user);
    }

    @Test
    void testDelete_ExistingUser() {
        when(mockStorage.remove(1L)).thenReturn(createTestUser(1L));

        userDAO.delete(1L);

        verify(mockStorage).remove(1L);
    }

    @Test
    void testDelete_NonExistingUser() {
        when(mockStorage.remove(1L)).thenReturn(null);

        userDAO.delete(1L);

        verify(mockStorage).remove(1L);
    }

    @Test
    void testGetAll() {
        User user1 = createTestUser(1L);
        User user2 = createTestUser(2L);

        List<User> users = Arrays.asList(user1, user2);
        when(mockStorage.values()).thenReturn(new HashSet<>(users));

        List<User> result = userDAO.getAll();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(users));
    }

    private User createTestUser(Long id) {
        Trainer user = new Trainer();
        user.setId(id);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setPassword("password");
        user.setActive(true);
        return user;
    }

}

