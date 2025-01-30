package uz.gym.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.gym.crm.dto.ChangePasswordDTO;
import uz.gym.crm.service.abstr.*;
import uz.gym.crm.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "api/users", produces = {"application/JSON", "application/XML"})
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        if (userService.authenticate(username, password)) {
            return JwtUtil.generateToken(username);
        } else {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    @PutMapping("/change-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        {
            String username = changePasswordDTO.getUsername();
            String oldPassword = changePasswordDTO.getOldPassword();
            String newPassword = changePasswordDTO.getNewPassword();
            userService.updateUser(username, oldPassword, newPassword);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password updated successfully");
            return ResponseEntity.ok(response);
        }
    }

    @PatchMapping("/activate")
    public ResponseEntity<Void> updateTraineeStatus(
            @RequestParam String username,
            @RequestParam boolean isActive) {
        if (isActive) {
            userService.activate(username);
        } else {
            userService.deactivate(username);
        }
        return ResponseEntity.ok().build();
    }
}