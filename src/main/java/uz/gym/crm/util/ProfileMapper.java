package uz.gym.crm.util;

import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;

public class ProfileMapper {
    public static void updateFields(Object existing, Object updated) {
        if (existing == null || updated == null) {
            throw new IllegalArgumentException("Existing and updated objects must not be null.");
        }
        if (existing.getClass() != updated.getClass()) {
            throw new IllegalArgumentException("Existing and updated objects must be of the same type.");
        }
        if (existing instanceof Trainee) {
            updateTraineeFields((Trainee) existing, (Trainee) updated);
        } else if (existing instanceof Trainer) {
            updateTrainerFields((Trainer) existing, (Trainer) updated);
        } else {
            throw new IllegalArgumentException("Unsupported profile type: " + existing.getClass().getName());
        }
    }

    private static void updateTraineeFields(Trainee existing, Trainee updated) {
        if (updated.getAddress() != null) {
            existing.setAddress(updated.getAddress());
        }
        if (updated.getDateOfBirth() != null) {
            existing.setDateOfBirth(updated.getDateOfBirth());
        }
    }

    private static void updateTrainerFields(Trainer existing, Trainer updated) {
        if (updated.getSpecialization() != null) {
            existing.setSpecialization(updated.getSpecialization());
        }
    }
}