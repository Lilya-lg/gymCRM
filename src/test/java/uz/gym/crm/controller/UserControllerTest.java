package uz.gym.crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import uz.gym.crm.dto.BaseUserDTO;
import uz.gym.crm.service.abstr.UserService;
import uz.gym.crm.util.JwtUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void auth_ShouldReturnLoginSuccessful_WhenCredentialsAreValid() {
        BaseUserDTO userDTO = new BaseUserDTO();
        userDTO.setUsername("validUser");
        userDTO.setPassword("validPassword");

        when(userService.authenticate("validUser", "validPassword")).thenReturn(true);

        ResponseEntity<String> response = userController.auth(userDTO, null);

        assertEquals(OK, response.getStatusCode());
        assertEquals("Login successful", response.getBody());
    }

    @Test
    void auth_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() {

        BaseUserDTO userDTO = new BaseUserDTO();
        userDTO.setUsername("invalidUser");
        userDTO.setPassword("wrongPassword");

        when(userService.authenticate("invalidUser", "wrongPassword")).thenReturn(false);

        ResponseEntity<String> response = userController.auth(userDTO, null);


        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody());
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        String username = "validUser";
        String password = "validPassword";
        String token = "generatedToken";

        when(userService.authenticate(username, password)).thenReturn(true);
        mockStatic(JwtUtil.class);
        when(JwtUtil.generateToken(username)).thenReturn(token);


        String response = userController.login(username, password);

        assertEquals(token, response);
        verify(userService).authenticate(username, password);
    }

    @Test
    void login_ShouldThrowException_WhenCredentialsAreInvalid() {

        String username = "invalidUser";
        String password = "wrongPassword";

        when(userService.authenticate(username, password)).thenReturn(false);


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userController.login(username, password);
        });

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void changePassword_ShouldReturnSuccessMessage_WhenAuthorizationIsValid() {

        String username = "validUser";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String authHeader = "Bearer validToken";

        doNothing().when(userService).updateUser(username, oldPassword, newPassword);


        ResponseEntity<String> response = userController.changePassword(username, oldPassword, newPassword, authHeader);

        assertEquals(OK, response.getStatusCode());
        assertEquals("Password updated successfully", response.getBody());
    }

    @Test
    void changePassword_ShouldReturnUnauthorized_WhenAuthHeaderIsInvalid() {

        String username = "validUser";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String authHeader = null;


        ResponseEntity<String> response = userController.changePassword(username, oldPassword, newPassword, authHeader);

        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertEquals("Missing or invalid Authorization header", response.getBody());
    }

    @Test
    void changePassword_ShouldReturnBadRequest_WhenServiceThrowsException() {

        String username = "validUser";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String authHeader = "Bearer validToken";

        doThrow(new IllegalArgumentException("Invalid password")).when(userService).updateUser(username, oldPassword, newPassword);


        ResponseEntity<String> response = userController.changePassword(username, oldPassword, newPassword, authHeader);


        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid password", response.getBody());
    }
}
