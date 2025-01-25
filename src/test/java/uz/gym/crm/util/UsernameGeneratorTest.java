package uz.gym.crm.util;

import org.junit.jupiter.api.Test;
import uz.gym.crm.domain.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UsernameGeneratorTest {

        @Test
        void generateUniqueUsernameWithNoConflicts() {
            User user = createTestUser(1L, "John", "Doe");
            List<User> existingUsers = new ArrayList<>();

            String username = UsernameGenerator.generateUniqueUsername(user, existingUsers);

            assertEquals("john.doe", username, "Username should be generated correctly without conflicts.");
        }

        @Test
        void generateUniqueUsernameWithConflicts() {
            User user = createTestUser(2L, "John", "Doe");
            List<User> existingUsers = new ArrayList<>();
            existingUsers.add(createTestUser(1L, "John", "Doe", "john.doe"));
            existingUsers.add(createTestUser(2L, "Jane", "Smith", "jane.smith"));

            String username = UsernameGenerator.generateUniqueUsername(user, existingUsers);

            assertEquals("john.doe1", username, "Username should resolve conflicts by appending a number.");
        }

        @Test
        void generateUniqueUsernameWithMultipleConflicts() {
            User user = createTestUser(3L, "John", "Doe");
            List<User> existingUsers = new ArrayList<>();
            existingUsers.add(createTestUser(1L, "John", "Doe", "john.doe"));
            existingUsers.add(createTestUser(2L, "John", "Doe", "john.doe1"));

            String username = UsernameGenerator.generateUniqueUsername(user, existingUsers);

            assertEquals("john.doe2", username, "Username should resolve multiple conflicts correctly.");
        }

        @Test
        void generateUniqueUsernameWithNullFirstNameOrLastName() {
            User user = createTestUser(3L, null, "Doe");
            List<User> existingUsers = new ArrayList<>();

            String username = UsernameGenerator.generateUniqueUsername(user, existingUsers);

            assertEquals("user.doe", username, "Username should default to 'user' for null first name.");

            user.setFirstName("John");
            user.setLastName(null);

            username = UsernameGenerator.generateUniqueUsername(user, existingUsers);

            assertEquals("john.3", username, "Username should default to ID for null last name.");
        }

        @Test
        void generateUniqueUsernameThrowsExceptionForNullUser() {
            assertThrows(IllegalArgumentException.class, () ->
                    UsernameGenerator.generateUniqueUsername(null, new ArrayList<>()));
        }

        private User createTestUser(Long id, String firstName, String lastName) {
            return createTestUser(id, firstName, lastName, null);
        }

        private User createTestUser(Long id, String firstName, String lastName, String username) {
            User user = new User() {};
            user.setId(id);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            return user;
        }
}
