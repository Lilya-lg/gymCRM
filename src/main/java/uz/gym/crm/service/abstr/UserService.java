package uz.gym.crm.service.abstr;


import uz.gym.crm.domain.User;

public interface UserService extends ProfileService<User> {
    void changePassword(String username, String oldPassword, String newPassword);

    boolean authenticate(String username, String password);
}
