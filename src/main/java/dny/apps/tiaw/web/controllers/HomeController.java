package dny.apps.tiaw.web.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import dny.apps.tiaw.service.GameAccService;
import dny.apps.tiaw.web.annotations.PageTitle;

@Controller
public class HomeController extends BaseController {
	private final GameAccService gameAccService;
	
	@Autowired
	public HomeController(GameAccService gameAccService) {
		this.gameAccService = gameAccService;
	}
	
	@GetMapping("/")
	@PreAuthorize("isAnonymous()")
	@PageTitle("Index")
	public ModelAndView index() {
		return super.view("index");
	}
	
	@GetMapping("/home")
	@PreAuthorize("isAuthenticated()")
	@PageTitle("Home")
	public ModelAndView home(Principal principal, ModelAndView modelAndView) {
		modelAndView.addObject("acc", this.gameAccService.findByUser(principal.getName()));
		return super.view("home", modelAndView);
	}
}
