package uz.gym.crm.service;

import uz.gym.crm.dao.BaseDAO;
import uz.gym.crm.domain.User;
import uz.gym.crm.util.PasswordGenerator;
import uz.gym.crm.util.UsernameGenerator;

import java.util.List;


public abstract class AbstractProfileService<T extends User> extends BaseServiceImpl<T> {
    private BaseDAO<User> userDAO;

    public AbstractProfileService(BaseDAO<T> dao, BaseDAO<User> userDAO) {
        super(dao);
        this.userDAO = userDAO;
    }

    protected void prepareUser(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            List<User> existingUsers = userDAO.getAll();
            String uniqueUsername = UsernameGenerator.generateUniqueUsername(user, existingUsers);
            user.setUsername(uniqueUsername);
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(PasswordGenerator.generatePassword());
        }
    }

    protected abstract User getUser(T entity);


}

