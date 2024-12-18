package uz.gym.crm.util;

import uz.gym.crm.domain.User;

import java.util.List;
import java.util.stream.Collectors;

public class UsernameGenerator {
    private UsernameGenerator() {
        // Private constructor to prevent instantiation
    }

    public static String generateUniqueUsername(User user, List<User> existingUsers) {
        String firstName = user.getFirstName() != null ? user.getFirstName() : "user";
        String lastName = user.getLastName() != null ? user.getLastName() : String.valueOf(user.getId());
        String baseUsername = (firstName + "." + lastName).toLowerCase();
        List<String> existingUsernames = existingUsers.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());

        String uniqueUsername = baseUsername;
        int counter = 1;

        while (existingUsernames.contains(uniqueUsername)) {
            uniqueUsername = baseUsername + counter++;
        }

        return uniqueUsername;
    }
}

