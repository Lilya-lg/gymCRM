package uz.gym.crm.util;

import org.junit.jupiter.api.Test;
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProfileMapperTest {

    @Test
    void updateFields_ShouldUpdateTraineeFields() {
        // Arrange
        User existingUser = new User();
        existingUser.setActive(false);

        Trainee existingTrainee = new Trainee();
        existingTrainee.setAddress("Old Address");
        existingTrainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        existingTrainee.setUser(existingUser);

        User updatedUser = new User();
        updatedUser.setActive(true);

        Trainee updatedTrainee = new Trainee();
        updatedTrainee.setAddress("New Address");
        updatedTrainee.setDateOfBirth(LocalDate.of(1995, 5, 15));
        updatedTrainee.setUser(updatedUser);


        ProfileMapper.updateFields(existingTrainee, updatedTrainee);

        assertEquals("New Address", existingTrainee.getAddress());
        assertEquals(LocalDate.of(1995, 5, 15), existingTrainee.getDateOfBirth());
        assertTrue(existingTrainee.getUser().isActive());
    }


    @Test
    void updateFields_ShouldThrowException_WhenExistingObjectIsNull() {

        Trainee updatedTrainee = new Trainee();


        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ProfileMapper.updateFields(null, updatedTrainee));

        assertEquals("Existing and updated objects must not be null.", exception.getMessage());
    }

    @Test
    void updateFields_ShouldThrowException_WhenUpdatedObjectIsNull() {

        Trainee existingTrainee = new Trainee();


        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ProfileMapper.updateFields(existingTrainee, null));

        assertEquals("Existing and updated objects must not be null.", exception.getMessage());
    }

    @Test
    void updateFields_ShouldThrowException_WhenObjectsAreDifferentTypes() {

        Trainee existingTrainee = new Trainee();
        Trainer updatedTrainer = new Trainer();


        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ProfileMapper.updateFields(existingTrainee, updatedTrainer));

        assertEquals("Existing and updated objects must be of the same type.", exception.getMessage());
    }

    @Test
    void updateFields_ShouldThrowException_ForUnsupportedType() {

        Object unsupportedExisting = new Object();
        Object unsupportedUpdated = new Object();


        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ProfileMapper.updateFields(unsupportedExisting, unsupportedUpdated));

        assertTrue(exception.getMessage().startsWith("Unsupported profile type:"));
    }
}
