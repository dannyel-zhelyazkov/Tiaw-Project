package dny.apps.tiaw.web.controller;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import dny.apps.tiaw.domain.models.view.DeckCardsViewModel;
import dny.apps.tiaw.domain.models.view.GameAccFightViewModel;
import dny.apps.tiaw.service.DeckService;
import dny.apps.tiaw.service.GameAccService;
import dny.apps.tiaw.service.UserService;

@Controller
public class GameAccController extends BaseController {
	private final GameAccService gameAccService;
	private final UserService userService;
	private final DeckService deckService;
	private final ModelMapper modelMapper;
	
	public GameAccController(GameAccService gameAccService, UserService userService, DeckService deckService, ModelMapper modelMapper) {
		this.gameAccService = gameAccService;
		this.userService = userService;
		this.deckService = deckService;
		this.modelMapper = modelMapper;
	}
	
	@PostMapping("/buy-card/{id}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView buyCardPost(@PathVariable String id, Principal principal) {
		this.gameAccService.buyCard(id, principal.getName());
		return super.redirect("/decks/deck");
	}
	
	@PostMapping("/delete-deck/{id}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView deleteDeckPost(@PathVariable String id, Principal principal) {
		this.gameAccService.removeDeck(id, principal.getName());
		this.deckService.deleteDeck(id);
		return super.redirect("/decks/deck");
	}
	
	@GetMapping("/fight")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView fightGet(ModelAndView modelAndView, Principal principal) {
		modelAndView.addObject("current", 
				this.modelMapper.map(this.gameAccService.findByUser(principal.getName()),
						GameAccFightViewModel.class));
		
		Type listGameAccFightViewModel = new TypeToken<List<GameAccFightViewModel>>(){}.getType(); 
		List<GameAccFightViewModel> gameAccFightViewModel = this.modelMapper
				.map(this.gameAccService.findAllGameAccs(), listGameAccFightViewModel);
		
		modelAndView.addObject("players", gameAccFightViewModel);
		return super.view("/fight/fight", modelAndView); 
	}
	
	@GetMapping("/inspect/{name}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView inspect(@PathVariable String name, ModelAndView modelAndView, Principal principal) {
		
		modelAndView.addObject("att", principal.getName());
		
		DeckCardsViewModel deckCardsViewModel = this.modelMapper.map(this.deckService.findDefenseDeckByOwner(name),
				DeckCardsViewModel.class);
		
		modelAndView.addObject("deck", deckCardsViewModel);
		modelAndView.addObject("cards", deckCardsViewModel.getCards());
		
		return super.view("/fight/inspect", modelAndView); 
	}
	
	@PostMapping("/set-defense/{id}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView setDefensePost(@PathVariable String id, Principal principal) {
		this.gameAccService.setDefense(id, principal.getName());
		return super.redirect("/decks/deck-cards/" + id);
	}
	
	@PostMapping("/set-attack/{id}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView setAttackPost(@PathVariable String id, Principal principal) {
		this.gameAccService.setAttack(id, principal.getName());
		return super.redirect("/decks/deck-cards/" + id);
	}
	
	@GetMapping("/fight-action/{att}/{def}")
	@ResponseBody
	public Map<String, GameAccFightViewModel> attackActionGet(@PathVariable String att, @PathVariable String def, Principal principal) {
		Map<String, GameAccFightViewModel> decks = new HashMap<>();
		
		decks.put("attacker", this.modelMapper.map(this.gameAccService.findByUser(att), GameAccFightViewModel.class));
		decks.put("defender", this.modelMapper.map(this.gameAccService.findByUser(def), GameAccFightViewModel.class));
		
		return decks;
	}
	
	@PostMapping("/won-fight")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView wonFightPost(Principal principal) {
		return super.redirect("/home");
	}
	
	@PostMapping("/lost-fight")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView lostFightPost(Principal principal) {
		return super.redirect("/home");
	}
}
	