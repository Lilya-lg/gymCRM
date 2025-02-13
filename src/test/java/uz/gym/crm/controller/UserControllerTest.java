package uz.gym.crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import uz.gym.crm.dto.ChangePasswordDTO;
import uz.gym.crm.service.abstr.UserService;


import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testLogin_Success() {
        when(userService.authenticate("testUser", "password123")).thenReturn(true);
        String token = userController.login("testUser", "password123").getBody().get("message").toString();
        assertNotNull(token);
    }

    @Test
    void testLogin_Failure() {
        when(userService.authenticate("testUser", "wrongPassword")).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> userController.login("testUser", "wrongPassword"));
    }

    @Test
    void testChangePassword() {
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setUsername("testUser");
        changePasswordDTO.setOldPassword("oldPass");
        changePasswordDTO.setNewPassword("newPass");
        doNothing().when(userService).changePassword("testUser", "oldPass", "newPass");

        ResponseEntity<Map<String, Object>> response = userController.changePassword(changePasswordDTO);

        assertNotNull(response.getBody());
        assertTrue((Boolean) response.getBody().get("success"));
        assertEquals("Password updated successfully", response.getBody().get("message"));
    }

    @Test
    void testUpdateTraineeStatus_Activate() {
        doNothing().when(userService).activate("testUser");

        ResponseEntity<Void> response = userController.updateTraineeStatus("testUser", true);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testUpdateTraineeStatus_Deactivate() {
        doNothing().when(userService).deactivate("testUser");

        ResponseEntity<Void> response = userController.updateTraineeStatus("testUser", false);
        assertEquals(200, response.getStatusCodeValue());
    }
}
