package uz.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uz.gym.crm.domain.User;
import uz.gym.crm.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void changePassword() {
        String username = "testUser";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        when(userRepository.updatePassword(username, oldPassword, newPassword)).thenReturn(1);

        userService.changePassword(username, oldPassword, newPassword);

        verify(userRepository, times(1)).updatePassword(username, oldPassword, newPassword);
    }

    @Test
    void authenticate() {
        String username = "testUser";
        String password = "password";

        when(userRepository.findByUsernameAndPassword(username, password)).thenReturn(Optional.of(new User()));

        boolean result = userService.authenticate(username, password);

        assertTrue(result);
    }

    @Test
    void activate() {
        String username = "testUser";
        User user = new User();
        user.setIsActive(false);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        userService.activate(username);

        assertTrue(user.getIsActive());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deactivate() {
        String username = "testUser";
        User user = new User();
        user.setIsActive(true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        userService.deactivate(username);

        assertFalse(user.getIsActive());
        verify(userRepository, times(1)).save(user);
    }
}