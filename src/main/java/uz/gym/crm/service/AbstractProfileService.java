package uz.gym.crm.service;

import uz.gym.crm.dao.BaseDAO;
import uz.gym.crm.domain.User;
import uz.gym.crm.util.PasswordGenerator;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractProfileService<T, ID> extends BaseServiceImpl<T, ID> {

    public AbstractProfileService(BaseDAO<T, ID> dao) {
        super(dao);
    }
    protected String generateUniqueUsername(String baseUsername, List<String> existingUsernames) {
        String uniqueUsername = baseUsername;
        int counter = 1;

        while (existingUsernames.contains(uniqueUsername)) {
            uniqueUsername = baseUsername + counter;
            counter++;
        }

        return uniqueUsername;
    }
    protected void prepareUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        String baseUsername = user.getFirstName().toLowerCase() + "." + user.getLastName().toLowerCase();
        List<String> existingUsernames = super.getAll().stream()
                .map(entity -> getUser(entity).getUsername())
                .collect(Collectors.toList());
        String uniqueUsername = generateUniqueUsername(baseUsername, existingUsernames);
        user.setUsername(uniqueUsername);
        user.setPassword(PasswordGenerator.generatePassword());
    }

    protected abstract User getUser(T entity);
}

