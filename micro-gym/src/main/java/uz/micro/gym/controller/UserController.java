package uz.micro.gym.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.micro.gym.dto.ChangePasswordDTO;
import uz.micro.gym.service.BlackListService;
import uz.micro.gym.service.abstr.UserService;
import uz.micro.gym.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "api/users", produces = {"application/json", "application/xml"})
public class UserController {

    private final UserService userService;
    private final BlackListService blackListService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, BlackListService blackListService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.blackListService = blackListService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        Map<String, Object> response = new HashMap<>();
        if (userService.authenticate(username, password)) {
            response.put("success", true);
            response.put("token", jwtUtil.generateToken(username));
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Invalid username or password");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String token) {
        blackListService.addToBlacklist(token.substring(7));
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        String username = changePasswordDTO.getUsername();
        String oldPassword = changePasswordDTO.getOldPassword();
        String newPassword = changePasswordDTO.getNewPassword();
        userService.changePassword(username, oldPassword, newPassword);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Password updated successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/activate")
    public ResponseEntity<Void> updateTraineeStatus(@RequestParam String username, @RequestParam boolean isActive) {
        if (isActive) {
            userService.activate(username);
        } else {
            userService.deactivate(username);
        }
        return ResponseEntity.ok().build();
    }



}
