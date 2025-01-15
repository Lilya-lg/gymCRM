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

}