package dny.apps.tiaw.web.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import dny.apps.tiaw.service.UserService;

@Controller
public class HomeController extends BaseController {
	private final UserService userService;
	
	@Autowired
	public HomeController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/")
	@PreAuthorize("isAnonymous()")
	public ModelAndView index() {
		return super.view("index");
	}
	
	@GetMapping("/home")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView home(Principal principal, ModelAndView modelAndView) {
		modelAndView.addObject("gold", this.userService.findUserByUsername(principal.getName()).getGameAcc().getGold());
		return super.view("home", modelAndView);
	}
}
