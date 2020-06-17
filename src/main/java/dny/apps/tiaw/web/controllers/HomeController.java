package dny.apps.tiaw.web.controllers;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import dny.apps.tiaw.domain.models.view.FightViewModel;
import dny.apps.tiaw.domain.models.view.SaleCardViewModel;
import dny.apps.tiaw.service.FightService;
import dny.apps.tiaw.service.SaleCardService;
import dny.apps.tiaw.service.UserService;
import dny.apps.tiaw.web.annotations.PageTitle;

@Controller
public class HomeController extends BaseController {
	private final FightService fightService;
	private final SaleCardService saleCardService;
	private final UserService userService;
	private final ModelMapper modelMapper;
	
	@Autowired
	public HomeController(FightService fightService, SaleCardService saleCardService, UserService userService, 
			ModelMapper modelMapper) {
		this.fightService = fightService;
		this.saleCardService = saleCardService;
		this.userService = userService;
		this.modelMapper = modelMapper;
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
		Type listFightViewModelType = new TypeToken<List<FightViewModel>>() {}.getType();
		Type listSaleCardViewModelType = new TypeToken<List<SaleCardViewModel>>() {}.getType();
		
		List<FightViewModel> fights = this.modelMapper.map(
				this.fightService.findAllByUser(principal.getName()), listFightViewModelType);
		
		List<SaleCardViewModel> cards = this.modelMapper.map(
				this.saleCardService.findAll(), listSaleCardViewModelType);
		
		modelAndView.addObject("cards", cards);
		modelAndView.addObject("fights", fights);
		modelAndView.addObject("username", principal.getName());
		modelAndView.addObject("gold", this.userService.findByUsername(principal.getName()).getGameAcc().getGold());
		
		return super.view("home", modelAndView);
	}
}
