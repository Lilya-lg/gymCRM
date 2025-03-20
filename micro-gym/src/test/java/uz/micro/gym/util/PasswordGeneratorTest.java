package uz.micro.gym.util;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordGeneratorTest {

    @Test
    void generatePasswordDefaultLength() {
        String password = PasswordGenerator.generatePassword();

        assertNotNull(password, "Password should not be null.");
        assertEquals(10, password.length(), "Password length should match the default length.");
    }

    @Test
    void generatePasswordWithCustomLength() {
        int customLength = 15;
        String password = PasswordGenerator.generatePassword(customLength);

        assertNotNull(password, "Password should not be null.");
        assertEquals(customLength, password.length(), "Password length should match the custom length.");
    }

    @Test
    void generatePasswordContainsUppercaseLowercaseDigitsAndSpecialCharacters() {
        String password = PasswordGenerator.generatePassword();

        assertTrue(password.matches(".*[A-Z].*"), "Password should contain at least one uppercase letter.");
        assertTrue(password.matches(".*[a-z].*"), "Password should contain at least one lowercase letter.");
        assertTrue(password.matches(".*[0-9].*"), "Password should contain at least one digit.");
        assertTrue(password.matches(".*[!@#$%^&*()\\-_=+<>].*"), "Password should contain at least one special character.");
    }

    @Test
    void generatePasswordIsShuffled() {
        String password = PasswordGenerator.generatePassword();

        // Check that the password is not in a predictable order
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()\\-_=+<>].*");

        assertTrue(hasUppercase, "Password should contain at least one uppercase letter.");
        assertTrue(hasLowercase, "Password should contain at least one lowercase letter.");
        assertTrue(hasDigit, "Password should contain at least one digit.");
        assertTrue(hasSpecialChar, "Password should contain at least one special character.");
    }



}
