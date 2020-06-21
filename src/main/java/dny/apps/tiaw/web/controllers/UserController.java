package dny.apps.tiaw.web.controllers;

import java.lang.reflect.Type;
import java.security.Principal;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import dny.apps.tiaw.domain.models.binding.UserRegisterBindingModel;
import dny.apps.tiaw.domain.models.binding.UserSearchBindingModel;
import dny.apps.tiaw.domain.models.service.UserRegisterServiceModel;
import dny.apps.tiaw.domain.models.view.UserProfileViewModel;
import dny.apps.tiaw.domain.models.view.UserViewModel;
import dny.apps.tiaw.error.user.UserNotFoundException;
import dny.apps.tiaw.service.UserService;
import dny.apps.tiaw.validation.controller.UserRegisterValidator;
import dny.apps.tiaw.validation.controller.UserSearchValidator;
import dny.apps.tiaw.web.annotations.PageTitle;

@Controller
@RequestMapping("/users")
public class UserController extends BaseController{
	
	private final UserService userService;
	private final UserRegisterValidator userRegisterValidator;
	private final UserSearchValidator userSearchValidator;
	private final ModelMapper modelMapper;
	
	@Autowired
	public UserController(UserService userService, UserRegisterValidator userRegisterValidator, UserSearchValidator userSearchValidator, ModelMapper modelMapper) {
		this.userService = userService;
		this.userRegisterValidator = userRegisterValidator;
		this.userSearchValidator = userSearchValidator;
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
	public ModelAndView registerPost(@ModelAttribute("bind") UserRegisterBindingModel model, BindingResult result) {
		
		this.userRegisterValidator.validate(model, result);
		
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
		return super.view("/user/profile", model);
	}
	
	@GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
	@PageTitle("Users")
    public ModelAndView allUsers(ModelAndView modelAndView, @RequestParam(defaultValue = "0") int page) {
       Type pageUserViewModelType = new TypeToken<Page<UserViewModel>>() {}.getType();
       
       Page<UserViewModel> users = this.modelMapper.map(
    		   this.userService.findAll(PageRequest.of(page, 10)), pageUserViewModelType);
       
       modelAndView.addObject("user", new UserSearchBindingModel());
       modelAndView.addObject("users", users);
       modelAndView.addObject("currentPage", page);
       return super.view("/user/all-users", modelAndView);
    }
	
	@PostMapping("/search")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView search(ModelAndView modelAndView, @ModelAttribute("user") UserSearchBindingModel model, BindingResult result, @RequestParam(defaultValue = "0") int page) {
    	
		this.userSearchValidator.validate(model, result);
		
		if(result.hasErrors()) {
				Type pageUserViewModelType = new TypeToken<Page<UserViewModel>>() {}.getType();
		       
		       Page<UserViewModel> users = this.modelMapper.map(
		    		   this.userService.findAll(PageRequest.of(page, 10)), pageUserViewModelType);
		       
		       modelAndView.addObject("users", users);
		       modelAndView.addObject("currentPage", page);
		       return super.view("/user/all-users", modelAndView);
		}
		
    	UserViewModel user = this.modelMapper.map(this.userService.findByUsername(model.getUsername()), UserViewModel.class);
    	
    	modelAndView.addObject("user", user);
    	
    	return super.view("/user/user-view", modelAndView);
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
    
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView userNotFound(UserNotFoundException ex, ModelAndView modelAndView) {
    	modelAndView.addObject("message", ex.getMessage());
    	modelAndView.addObject("error", "User Error");
    	
    	return super.view("erro", modelAndView);
    }
}
