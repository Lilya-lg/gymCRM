package uz.micro.gym.service.abstr;



import uz.micro.gym.domain.User;

public interface UserService extends ProfileService<User> {
    void changePassword(String username, String oldPassword, String newPassword);

    boolean authenticate(String username, String password);
}
