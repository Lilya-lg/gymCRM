package uz.gym.crm.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TraineeTest {

    @Test
    void testGettersAndSetters() {
        // Create User
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setPassword("password123");

        // Create Trainee
        Trainee trainee = new Trainee();
        trainee.setId(100L);
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setUser(user);

        // Assert ID
        assertEquals(100L, trainee.getId());

        // Assert Date of Birth
        assertEquals(LocalDate.of(1990, 1, 1), trainee.getDateOfBirth());

        // Assert Address
        assertEquals("123 Main St", trainee.getAddress());

        // Assert User
        assertEquals(user, trainee.getUser());
        assertEquals("johndoe", trainee.getUser().getUsername());
    }


    @Test
    void testToString() {
        // Create User
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");

        // Create Trainee
        Trainee trainee = new Trainee();
        trainee.setId(100L);
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setUser(user);

        // Assert ToString contains relevant information
        String toString = trainee.toString();
        assertTrue(toString.contains("100"));
        assertTrue(toString.contains("123 Main St"));
        assertTrue(toString.contains("johndoe"));
    }
}