package uz.gym.crm.domain;


import org.junit.jupiter.api.Test;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TraineeTest {

    @Test
    void testGettersAndSetters() {

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setPassword("password123");


        Trainee trainee = new Trainee();
        trainee.setId(100L);
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setUser(user);


        assertEquals(100L, trainee.getId());


        assertEquals(LocalDate.of(1990, 1, 1), trainee.getDateOfBirth());


        assertEquals("123 Main St", trainee.getAddress());


        assertEquals(user, trainee.getUser());
        assertEquals("johndoe", trainee.getUser().getUsername());
    }


    @Test
    void testToString() {

        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");


        Trainee trainee = new Trainee();
        trainee.setId(100L);
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setUser(user);


        String toString = trainee.toString();
        assertTrue(toString.contains("100"));
        assertTrue(toString.contains("123 Main St"));
        assertTrue(toString.contains("johndoe"));
    }
}