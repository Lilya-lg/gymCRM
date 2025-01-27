package uz.gym.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uz.gym.crm.dto.BaseUserDTO;
import uz.gym.crm.service.abstr.*;
import uz.gym.crm.util.JwtUtil;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "api/users", produces = {"application/JSON", "application/XML"})
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<String> auth(@Valid @RequestBody BaseUserDTO userDTO, BindingResult result) {
        {
            if (userService.authenticate(userDTO.getUsername(), userDTO.getPassword())) {
                return ResponseEntity.ok("Login successful");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

    }

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
    public ResponseEntity<String> changePassword(@RequestParam("username") String username,
                                                 @RequestParam("oldPassword") String oldPassword,
                                                 @RequestParam("newPassword") String newPassword) {
        {
            try {
                userService.updateUser(username, oldPassword, newPassword);
                return ResponseEntity.ok("Password updated successfully");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
            }

        }
    }
}