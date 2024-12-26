package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uz.gym.crm.dao.BaseDAO;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.User;
import uz.gym.crm.service.BaseServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class BaseServiceImplTest {

    private BaseDAO<Trainee> mockDao;
    private BaseServiceImpl<Trainee> service;

    @BeforeEach
    void setUp() {
        mockDao = Mockito.mock(BaseDAO.class);
        service = new BaseServiceImpl<>(mockDao) {};
    }

    @Test
    void create_ShouldSaveEntity() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        trainee.setUser(user);

        service.create(trainee);

        verify(mockDao, times(1)).save(trainee);
    }

    @Test
    void read_ShouldReturnEntity_WhenExists() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        trainee.setUser(user);

        when(mockDao.read(1L)).thenReturn(Optional.of(trainee));

        Trainee result = service.read(1L);

        assertNotNull(result);
        assertEquals("John", result.getUser().getFirstName());
        assertEquals("Doe", result.getUser().getLastName());
        verify(mockDao, times(1)).read(1L);
    }

    @Test
    void read_ShouldThrowException_WhenEntityDoesNotExist() {
        when(mockDao.read(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.read(1L));
        assertEquals("Entity not found for ID: 1", exception.getMessage());
        verify(mockDao, times(1)).read(1L);
    }

    @Test
    void update_ShouldUpdateEntity_WhenEntityExists() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        trainee.setUser(user);

        when(mockDao.existsById(1L)).thenReturn(true);

        service.update(trainee);

        verify(mockDao, times(1)).existsById(1L);
        verify(mockDao, times(1)).save(trainee);
    }

    @Test
    void update_ShouldThrowException_WhenEntityDoesNotExist() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        trainee.setUser(user);

        when(mockDao.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.update(trainee));
        assertEquals("Entity not found for update: ID=1", exception.getMessage());
        verify(mockDao, times(1)).existsById(1L);
        verify(mockDao, times(0)).save(any());
    }

    @Test
    void delete_ShouldDeleteEntity() {
        service.delete(1L);

        verify(mockDao, times(1)).delete(1L);
    }

    @Test
    void getAll_ShouldReturnAllEntities() {
        Trainee trainee1 = new Trainee();
        trainee1.setId(1L);
        User user1 = new User();
        user1.setFirstName("John");
        user1.setLastName("Doe");
        trainee1.setUser(user1);

        Trainee trainee2 = new Trainee();
        trainee2.setId(2L);
        User user2 = new User();
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        trainee2.setUser(user2);

        when(mockDao.getAll()).thenReturn(List.of(trainee1, trainee2));

        List<Trainee> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getUser().getFirstName());
        assertEquals("Jane", result.get(1).getUser().getFirstName());
        verify(mockDao, times(1)).getAll();
    }
}
