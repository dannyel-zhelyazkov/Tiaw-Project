package dny.apps.tiaw.web.controllers;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import dny.apps.tiaw.domain.models.service.GameAccServiceModel;
import dny.apps.tiaw.domain.models.view.DeckCardsViewModel;
import dny.apps.tiaw.domain.models.view.GameAccAttackViewModel;
import dny.apps.tiaw.domain.models.view.GameAccDefenseViewModel;
import dny.apps.tiaw.service.CardService;
import dny.apps.tiaw.service.DeckService;
import dny.apps.tiaw.service.GameAccService;
import dny.apps.tiaw.service.UserService;
import dny.apps.tiaw.web.annotations.PageTitle;

@Controller
@RequestMapping("/fights")
public class FightController extends BaseController {
	private final GameAccService gameAccService;
	private final DeckService deckService;
	private final ModelMapper modelMapper;
	
	@Autowired
	public FightController(GameAccService gameAccService, UserService userService, DeckService deckService, CardService cardService, ModelMapper modelMapper) {
		this.gameAccService = gameAccService;
		this.deckService = deckService;
		this.modelMapper = modelMapper;
	}
	
	@GetMapping("/fight")
	@PreAuthorize("isAuthenticated()")
	@PageTitle("Fight")
	public ModelAndView fightGet(ModelAndView modelAndView, Principal principal,  @RequestParam(defaultValue = "0") int page) {
		GameAccAttackViewModel currentPlayer = this.modelMapper.map(this.gameAccService.findByUser(principal.getName()), GameAccAttackViewModel.class);
		modelAndView.addObject("current", currentPlayer);
		
		Type pageGameAccDefenseViewModelType = new TypeToken<Page<GameAccDefenseViewModel>>(){}.getType();
		
		Page<GameAccDefenseViewModel> players = this.modelMapper
				.map(this.gameAccService.findAllGameAccFightModels(PageRequest.of(page, 10, Sort.by("battlePoints").descending())), pageGameAccDefenseViewModelType);
		
		modelAndView.addObject("players", players);
		modelAndView.addObject("currentPage", page);
		return super.view("/fight/fight", modelAndView); 
	}
	
	@GetMapping("/inspect/{name}")
	@PreAuthorize("isAuthenticated()")
	@PageTitle("Inspect")
	public ModelAndView inspect(@PathVariable String name, ModelAndView modelAndView, Principal principal) {
		
		modelAndView.addObject("att", principal.getName());
		modelAndView.addObject("attackTickets", this.gameAccService.findByUser(principal.getName()).getAttackTickets());
		
		DeckCardsViewModel deckCardsViewModel = this.modelMapper.map(this.deckService.findDefenseDeckByOwner(name),
				DeckCardsViewModel.class);
		
		modelAndView.addObject("deck", deckCardsViewModel);
		modelAndView.addObject("cards", deckCardsViewModel.getCards());
		
		return super.view("/fight/inspect", modelAndView); 
	}
	
	@PostMapping("/won-fight/{defender}/{ubp}/{ebp}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView wonFightPost(@PathVariable String defender, @PathVariable String ubp, @PathVariable String ebp, Principal principal) {
		this.gameAccService.wonFight(defender, principal.getName(), ubp, ebp);
		return super.redirect("/home");
	}
	
	@PostMapping("/lost-fight/{defender}/{ubp}/{ebp}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView lostFightPost(@PathVariable String defender, @PathVariable String ubp, @PathVariable String ebp, Principal principal) {
		this.gameAccService.lostFight(defender, principal.getName(), ubp, ebp);
		return super.redirect("/home");
	}
	
	@GetMapping("/fight-action/{att}/{def}")
	@PreAuthorize("isAuthenticated()")
	@ResponseBody
	public Map<String, GameAccServiceModel> fightAction(@PathVariable String att, @PathVariable String def) {
		Map<String, GameAccServiceModel> map = new LinkedHashMap<>();
		
		map.put("attacker", this.gameAccService.findByUser(att));
		map.put("defender", this.gameAccService.findByUser(def));
		
		return map;
	}
}
