package dny.apps.tiaw.web.controller;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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

import dny.apps.tiaw.domain.models.binding.DeckAddBindingModel;
import dny.apps.tiaw.domain.models.service.CardServiceModel;
import dny.apps.tiaw.domain.models.service.DeckServiceModel;
import dny.apps.tiaw.domain.models.view.DeckViewModel;
import dny.apps.tiaw.domain.models.view.DeckCardsViewModel;
import dny.apps.tiaw.service.CardService;
import dny.apps.tiaw.service.DeckService;
import dny.apps.tiaw.service.GameAccService;
import dny.apps.tiaw.service.UserService;

@Controller
@RequestMapping("/decks")
public class DeckController extends BaseController {
	private final UserService userService;
	private final DeckService deckService;
	private final GameAccService gameAccService;
	private final CardService cardService;
	private final ModelMapper modelMapper;

	@Autowired
	public DeckController(UserService userService, DeckService deckService, GameAccService gameAccService, CardService cardService, ModelMapper modelMapper) {
		this.userService = userService;
		this.deckService = deckService;
		this.gameAccService = gameAccService;
		this.cardService = cardService;
		this.modelMapper = modelMapper;
	}
	
	@GetMapping("/deck")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView deck(ModelAndView modelAndView, Principal principal) {
		Type listDeckViewModelType = new TypeToken<List<DeckViewModel>>() {}.getType();
		
		List<DeckViewModel> deckViewModels = this.modelMapper
				.map(this.deckService.findAllDecksByOwner(principal.getName()), listDeckViewModelType);
		
		modelAndView.addObject("deckBind", new DeckAddBindingModel());
		modelAndView.addObject("decks", deckViewModels);
		modelAndView.addObject("username", principal.getName());
		return super.view("/deck/deck", modelAndView);
	}
	
	@PostMapping("/deck")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView addDeckPost(@Valid @ModelAttribute("deckBind") DeckAddBindingModel model, Principal principal, BindingResult result, ModelAndView modelAndView) {
		if(result.hasErrors()) {
			modelAndView.addObject("deckBind", model);
			return super.view("/deck/deck", modelAndView);
		}
		DeckServiceModel deckServiceModel = this.deckService.createDeck(this.modelMapper.map(model, DeckServiceModel.class));
		this.gameAccService.addDeck(deckServiceModel.getId(), principal.getName());
		return super.redirect("deck");
	}
	
	@GetMapping("/deck-cards/{id}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView viewDeckGet(@PathVariable String id, ModelAndView modelAndView, Principal principal) {
		DeckCardsViewModel deckCardsViewModel = this.modelMapper
				.map(this.deckService.findById(id), DeckCardsViewModel.class);
		
		if(this.userService.findUserByUsername(principal.getName()).getGameAcc().getDefenseDeck() != null && 
				this.userService.findUserByUsername(principal.getName()).getGameAcc().getDefenseDeck().getId().equals(deckCardsViewModel.getId())) {
			modelAndView.addObject("defense", true);
		}else {
			modelAndView.addObject("defense", false);
		}
		
		if(this.userService.findUserByUsername(principal.getName()).getGameAcc().getAttackDeck() != null && 
				this.userService.findUserByUsername(principal.getName()).getGameAcc().getAttackDeck().getId().equals(deckCardsViewModel.getId())) {
			modelAndView.addObject("attack", true);
		}else {
			modelAndView.addObject("attack", false);
		}
		
		modelAndView.addObject("deck", deckCardsViewModel);
		modelAndView.addObject("cards", deckCardsViewModel.getCards());
		modelAndView.addObject("deckId", id);
		
		return super.view("/deck/deck-cards", modelAndView);
	}
	
	@PostMapping("/add-to-deck/{cardId}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView addToDeckPost(@PathVariable String cardId, @ModelAttribute DeckAddBindingModel model, Principal principal) {
		this.deckService.addCard(model.getName(), cardId, principal.getName());				
		return super.redirect("/decks/deck");
	}
	
	@PostMapping("/remove-from-deck/{card}/{deck}")
	@PreAuthorize("isAuthenticated()")
	public ModelAndView removeCardFromDeck(@PathVariable("card") String card, @PathVariable("deck") String deck) {
		DeckServiceModel deckServiceModel = this.modelMapper.map(this.deckService
				.findById(deck), DeckServiceModel.class);
				
		this.deckService.removeCard(deckServiceModel, 
				this.modelMapper.map(this.cardService.findById(card), CardServiceModel.class));
	
		return super.redirect("/decks/deck-cards/" + deck);
	}
}
