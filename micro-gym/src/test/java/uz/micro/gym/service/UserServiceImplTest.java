package uz.micro.gym.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.micro.gym.domain.User;
import uz.micro.gym.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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