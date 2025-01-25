package uz.gym.crm.service.abstr;

import uz.gym.crm.domain.User;


public interface UserService extends ProfileService<User> {
    void updateUser(String username, String oldPassword, String newPassword);
}
