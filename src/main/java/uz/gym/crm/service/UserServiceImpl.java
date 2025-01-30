package uz.gym.crm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.gym.crm.dao.abstr.BaseDAO;
import uz.gym.crm.dao.abstr.TrainingDAO;
import uz.gym.crm.dao.abstr.UserDAO;
import uz.gym.crm.domain.User;
import uz.gym.crm.service.abstr.AbstractProfileService;
import uz.gym.crm.service.abstr.UserService;

@Service
@Transactional
public class UserServiceImpl extends AbstractProfileService<User> implements UserService {
    @Autowired
    UserService userService;

    public UserServiceImpl(BaseDAO<User> dao, UserDAO userDAO, TrainingDAO trainingDAO) {
        super(dao, userDAO, trainingDAO);
    }


    @Override
    public void updateUser(String username, String oldPassword, String newPassword) {
        super.changePassword(username, oldPassword, newPassword);
    }

    @Override
    protected User getUser(User entity) {
        return null;
    }
}
