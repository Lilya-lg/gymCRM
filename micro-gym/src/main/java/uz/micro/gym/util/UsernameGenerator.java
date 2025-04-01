package uz.micro.gym.util;

import uz.micro.gym.domain.User;

import java.util.List;
import java.util.stream.Collectors;

public class UsernameGenerator {
    private UsernameGenerator() {
    }

    public static <T extends User> String generateUniqueUsername(T user, List<? extends User> existingUsers) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        String firstName = user.getFirstName() != null ? user.getFirstName() : "user";
        String lastName = user.getLastName() != null ? user.getLastName() : String.valueOf(user.getId());


        String baseUsername = (firstName + "." + lastName).toLowerCase();

        List<String> existingUsernames = existingUsers.stream().map(User::getUsername).collect(Collectors.toList());


        String uniqueUsername = baseUsername;
        int counter = 1;

        while (existingUsernames.contains(uniqueUsername)) {
            uniqueUsername = baseUsername + counter++;
        }

        return uniqueUsername;
    }

}

