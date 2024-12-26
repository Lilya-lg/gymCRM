package uz.gym.crm.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import uz.gym.crm.domain.User;

import static org.junit.jupiter.api.Assertions.*;

class TrainerTest {

    @Test
    void testGettersAndSetters() {
        // Create User
        User user = new User();
        user.setId(1L);
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setUsername("janesmith");
        user.setPassword("password123");

        // Create Trainer
        Trainer trainer = new Trainer();
        trainer.setId(100L);
        trainer.setSpecialization("Fitness");
        trainer.setUser(user);

        // Assert ID
        assertEquals(100L, trainer.getId());

        // Assert Specialization
        assertEquals("Fitness", trainer.getSpecialization());

        // Assert User
        assertEquals(user, trainer.getUser());
        assertEquals("janesmith", trainer.getUser().getUsername());
    }


}
