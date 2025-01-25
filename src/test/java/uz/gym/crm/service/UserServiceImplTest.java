package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uz.gym.crm.dao.abstr.BaseDAO;
import uz.gym.crm.dao.abstr.TrainingDAO;
import uz.gym.crm.dao.abstr.UserDAO;
import uz.gym.crm.domain.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private BaseDAO<User> mockBaseDAO;
    private UserDAO mockUserDAO;
    private TrainingDAO mockTrainingDAO;
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        mockBaseDAO = Mockito.mock(BaseDAO.class);
        mockUserDAO = Mockito.mock(UserDAO.class);
        mockTrainingDAO = Mockito.mock(TrainingDAO.class);
        service = new UserServiceImpl(mockBaseDAO, mockUserDAO, mockTrainingDAO);
    }

    @Test
    void updateUser_ShouldChangePassword() {
        String username = "testUser";
        String oldPassword = "oldPass";
        String newPassword = "newPass";

        User user = new User();
        user.setUsername(username);
        user.setPassword(oldPassword);

        when(mockUserDAO.findByUsernameAndPassword(username, oldPassword)).thenReturn(java.util.Optional.of(user));
        doNothing().when(mockUserDAO).update(user);


        service.updateUser(username, oldPassword, newPassword);


        assertEquals(newPassword, user.getPassword(), "Password should be updated to the new password");
        verify(mockUserDAO, times(1)).findByUsernameAndPassword(username, oldPassword);
        verify(mockUserDAO, times(1)).updateUser(user);
    }

    @Test
    void updateUser_ShouldThrowException_WhenOldPasswordIsIncorrect() {
        String username = "testUser";
        String oldPassword = "wrongPass";
        String newPassword = "newPass";

        when(mockUserDAO.findByUsernameAndPassword(username, oldPassword)).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.updateUser(username, oldPassword, newPassword));

        assertEquals("Invalid username or password.", exception.getMessage());
        verify(mockUserDAO, times(1)).findByUsernameAndPassword(username, oldPassword);
        verify(mockUserDAO, never()).update(any(User.class));
    }

    @Test
    void getUser_ShouldReturnNull() {

        User user = new User();

        User result = service.getUser(user);


        assertNull(result, "getUser should always return null as per implementation");
    }
}

