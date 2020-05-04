package dny.apps.tiaw.service;

import dny.apps.tiaw.domain.models.service.UserServiceModel;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserServiceModel registerUser(UserServiceModel userServiceModel);

    UserServiceModel findUserByUsername(String username);

    UserServiceModel editUserProfile(UserServiceModel userServiceModel, String oldPassword);

    List<UserServiceModel> findAllUsers();

    void setUserRole(String id, String role);
}
