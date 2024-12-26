package uz.gym.crm.domain;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testGettersAndSetters() {
        // Create User
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setPassword("password123");
        user.setActive(true);

        // Assert ID
        assertEquals(1L, user.getId());

        // Assert First Name
        assertEquals("John", user.getFirstName());

        // Assert Last Name
        assertEquals("Doe", user.getLastName());

        // Assert Username
        assertEquals("johndoe", user.getUsername());

        // Assert Password
        assertEquals("password123", user.getPassword());

        // Assert Active Status
        assertTrue(user.isActive());
    }

}

