package dny.apps.tiaw.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import dny.apps.tiaw.domain.models.service.UserRegisterServiceModel;
import dny.apps.tiaw.domain.models.service.UserServiceModel;

public interface UserService extends UserDetailsService {
    UserServiceModel registerUser(UserRegisterServiceModel userRegisterServiceModel);

    UserServiceModel findByUsername(String username);

    UserServiceModel editUserProfile(UserServiceModel userServiceModel, String oldPassword);

    List<UserServiceModel> findAllUsers();

    void setUserRole(String id, String role);
}
