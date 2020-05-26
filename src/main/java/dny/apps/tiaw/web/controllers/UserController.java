package dny.apps.tiaw.web.controllers;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import dny.apps.tiaw.domain.models.binding.UserRegisterBindingModel;
import dny.apps.tiaw.domain.models.service.RoleServiceModel;
import dny.apps.tiaw.domain.models.service.UserRegisterServiceModel;
import dny.apps.tiaw.domain.models.view.UserProfileViewModel;
import dny.apps.tiaw.domain.models.view.UserViewModel;
import dny.apps.tiaw.service.UserService;
import dny.apps.tiaw.web.annotations.PageTitle;

@Controller
@RequestMapping("/users")
public class UserController extends BaseController{
	
	private final UserService userService;
	private final ModelMapper modelMapper;
	
	@Autowired
	public UserController(UserService userService, ModelMapper modelMapper) {
		this.userService = userService;
		this.modelMapper = modelMapper;
	}
	
	@GetMapping("/register")
	@PreAuthorize("isAnonymous()")
	@PageTitle("Register")
	public ModelAndView register(ModelAndView modelAndView) {
		modelAndView.addObject("bind", new UserRegisterBindingModel());
		return super.view("register", modelAndView);
	}
	
	@PostMapping("/register")
	@PreAuthorize("isAnonymous()")
	public ModelAndView registerPost(@Valid @ModelAttribute("bind") UserRegisterBindingModel model, BindingResult result) {
		
		if(result.hasErrors()) {
			return super.view("register");
		}
		
		this.userService.registerUser(this.modelMapper.map(model, UserRegisterServiceModel.class));						
		
		return super.view("/login");
	}
	
	@GetMapping("/login")
    @PreAuthorize("isAnonymous()")
	@PageTitle("Login")
    public ModelAndView login() {
        return super.view("login");
    }
	
	@GetMapping("/profile")
	@PreAuthorize("isAuthenticated()")
	@PageTitle("Profile")
	public ModelAndView profile(Principal principal, ModelAndView model) {
		model.addObject("model", 
				this.modelMapper.map(this.userService.findByUsername(principal.getName()), 
					UserProfileViewModel.class));
		return super.view("profile", model);
	}
	
	@GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
	@PageTitle("Users")
    public ModelAndView allUsers(ModelAndView modelAndView) {
        List<UserViewModel> users = this.userService.findAllUsers().stream()
                .map(u -> {
                    UserViewModel user = this.modelMapper.map(u, UserViewModel.class);
                    user.setAuthorities(u.getAuthorities().stream()
                            .map(RoleServiceModel::getAuthority)
                            .collect(Collectors.toSet()));

                    return user;
                }).collect(Collectors.toList());

        modelAndView.addObject("users", users);

        return super.view("all-users", modelAndView);
    }

    @PostMapping("/set-user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setUser(@PathVariable String id){
        this.userService.setUserRole(id, "user");

        return super.redirect("/users/all");
    }

    @PostMapping("/set-moderator/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setModerator(@PathVariable String id){
        this.userService.setUserRole(id, "moderator");

        return super.redirect("/users/all");
    }

    @PostMapping("/set-admin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setAdmin(@PathVariable String id){
        this.userService.setUserRole(id, "admin");

        return super.redirect("/users/all");
    }
}
